package server;

import request.Request;
import response.Response;
import app.Application;
import httprequest.HTTPRequest;
import httpresponse.HTTPResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by matthewhiggins on 7/27/16.
 */
public class ConnectionHandler implements Runnable {

    private Socket clientSocket;
    private Application application;

    private ConnectionHandler(Application app) {
        application = app;
    }

    private ConnectionHandler(Socket socket, Application app) {
        clientSocket = socket;
        application = app;
    }

    public static ConnectionHandler getNewConnectionHandler(Socket socket, Application app) {
        return new ConnectionHandler(socket, app);
    }

    public static ConnectionHandler getNewTestConnectionHandler(Application app) {
        return new ConnectionHandler(app);
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            HTTPRequest request = buildHttpRequest(reader);
            generateOutput(request, new PrintStream(clientSocket.getOutputStream()));
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

     static HTTPRequest buildHttpRequest(BufferedReader reader) throws IOException {
         HTTPRequest request = new HTTPRequest(readInFirstLine(reader));
         if (reader.ready()) {
             request.setHeaders(readInHeaders(reader));
         }
         if (reader.ready() && request.containsHeader("Content-Length")) {
             request.setBody(readInBody(reader, Integer.parseInt(request.getHeader("Content-Length"))));
         }
         return request;
     }

    public static String readInFirstLine(BufferedReader br) throws IOException {
        String input = "";
        input = br.readLine();
        return input.trim();
    }

    public static String readInHeaders(BufferedReader br) throws IOException {
        String input = "";
        String currentLine = br.readLine();
        while (currentLine != null && !currentLine.trim().isEmpty()) {
            input += currentLine.trim() + "\n";
            currentLine = br.readLine();
        }
        return input;
    }

    static String readInBody(BufferedReader reader, int contentLength) throws IOException {
        char[] bodyInChars = new char[contentLength];
        reader.read(bodyInChars);
        return new String(bodyInChars);
    }

     void generateOutput(Request request, PrintStream out) throws IOException {
        Response response = application.getResponse(request, new HTTPResponse());
        out.write(response.getAllButBody().getBytes());
        out.write(response.getBody());
    }
}

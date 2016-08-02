package server; /**
 * Created by matthewhiggins on 7/5/16.
 */

import app.Application;
import cobspecapp.CobSpecApp;
import httprequest.HTTPRequestFactory;
import httpresponse.HTTPResponseFactory;
import request.RequestFactory;
import response.ResponseFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    static String publicDirectory = "/Users/matthewhiggins/Desktop/cob_spec/public";
    static int myPort = 5000;

    private static ExecutorService executor = Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws IOException {
        setOptions(args);
        runServer(new CobSpecApp(publicDirectory), new HTTPRequestFactory(), new HTTPResponseFactory());
    }

    public static void runServer(Application app, RequestFactory requestFactory, ResponseFactory responseFactory) throws IOException {
        ServerSocket server = new ServerSocket(myPort);
        try {
            while (true) {
                Socket socket = server.accept();
                Runnable connectionHandler = ConnectionHandler.getNewConnectionHandler(socket, app, requestFactory, responseFactory);
                executor.execute(connectionHandler);
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + myPort);
            System.exit(-1);
        } finally {
            server.close();
        }
    }

    private static void setOptions(String[] args) {
        HashMap<String, String> options = CommandLineArgsParser.groupOptions(args);
        myPort = getPortNumber(options);
        publicDirectory = setPublicDirectory(options);
    }

     static int getPortNumber(HashMap<String, String> options) {
        int port = myPort;
        if (options.containsKey("-p")) {
            try {
                port = Integer.parseInt(options.get("-p"));
            } catch (NumberFormatException e) {
                port = myPort;
            }
        }
        return port;
    }

    static String setPublicDirectory(HashMap<String, String> options) {
        String directory = publicDirectory;
        String directoryFromOptions = options.get("-d");
        if (directoryFromOptions == null) {
            directory = publicDirectory;
        } else {
            File file = new File(directoryFromOptions);
            if (file.exists()) {
                directory = directoryFromOptions;
            }
        }
        return directory;
    }
}

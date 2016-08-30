package com.github.mh120888.cobspecapp;

import com.github.mh120888.httpmessage.HTTPRequest;
import com.github.mh120888.httpmessage.HTTPResponse;

public class CoffeeAction implements Action {
    public CoffeeAction() {}

    public HTTPResponse getResponse(HTTPRequest request, HTTPResponse response) {
        response.setStatus(418);
        response.setBody("I'm a teapot".getBytes());
        return response;
    }
}

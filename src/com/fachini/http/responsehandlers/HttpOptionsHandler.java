package com.fachini.http.responsehandlers;

import com.fachini.http.HttpRequest;
import com.fachini.http.HttpResponse;

public class HttpOptionsHandler extends HttpResponse {

    @Override
    public void handle(HttpRequest request) {
        addHeader("Allow", "OPTIONS, GET, HEAD, POST");
    }

}

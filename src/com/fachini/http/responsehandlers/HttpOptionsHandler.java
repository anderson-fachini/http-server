package com.fachini.http.responsehandlers;

import com.fachini.http.HttpRequest;
import com.fachini.http.HttpResponse;

public class HttpOptionsHandler extends HttpResponse {

	public HttpOptionsHandler(HttpRequest request) {
		super(request);
	}

	@Override
	public void handle() {
		addHeader("Allow", "OPTIONS, GET, HEAD, POST");
	}

}

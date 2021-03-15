package com.fachini.http.responsehandlers;

import com.fachini.http.HttpRequest;
import com.fachini.http.HttpResponse;

public class SayHelloHandler extends HttpResponse {

	@Override
	public void handle(HttpRequest request) {
		String name = request.getQueryStringParameter("name");
		if (name == null || name.isBlank()) {
			name = "Anonymous";
		}

		String content = "Hello " + name;
		setContent(content);
	}

}

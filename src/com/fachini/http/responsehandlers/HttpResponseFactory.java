package com.fachini.http.responsehandlers;

import java.util.HashMap;
import java.util.Map;

import com.fachini.http.HttpMethod;
import com.fachini.http.HttpRequest;
import com.fachini.http.HttpResponse;

public class HttpResponseFactory {

	private static final Map<String, HttpResponse> PATH_HANDLERS = new HashMap<>();

	private HttpResponseFactory() {
	}

	public static void registerHandlerForPath(String path, HttpResponse handler) {
		PATH_HANDLERS.put(path, handler);
	}

	public static HttpResponse getResponseFor(HttpRequest request) {
		HttpResponse pathHandler = PATH_HANDLERS.get(request.getPath());
		if (pathHandler != null) {
			return pathHandler;
		}

		if (request.getHttpMethod() == HttpMethod.OPTIONS) {
			return new HttpOptionsHandler(request);
		}

		return new FileHandler(request);
	}
}

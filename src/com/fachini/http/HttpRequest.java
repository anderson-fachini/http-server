package com.fachini.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

	private Map<String, String> headers = new LinkedHashMap<>();
	private Map<String, String> queryStringParamters = new LinkedHashMap<>();
	private HttpMethod httpMethod;
	private String path;
	private String body = "";
	private String httpVersion;

	public HttpRequest(InputStream inputStream) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			boolean readMethod = false;
			boolean readAllHeaders = false;
			StringBuilder sb = new StringBuilder();

			while (!(line = br.readLine()).isBlank()) {
				if (!readMethod) {
					String[] lineSplitted = line.split(" ");

					setHttpMethod(HttpMethod.valueOf(lineSplitted[0]));
					setPath(lineSplitted[1]);
					checkQueryStringParameters(lineSplitted[1]);
					setHttpVersion(lineSplitted[2]);
					readMethod = true;
					continue;
				}

				if (line.isBlank()) {
					readAllHeaders = true;
					continue;
				}

				if (!readAllHeaders) {
					int colonIdx = line.indexOf(": ");
					String key = line.substring(0, colonIdx);
					String value = line.substring(colonIdx + 2);
					addHeader(key, value);
				} else {
					sb.append(line).append(System.lineSeparator());
				}
			}
		} catch (IOException e) {
			Logger.log("Error reading request", e);
		}
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
		headers.put("content-type", "plain/text");
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		int questionMarkIdx = path.indexOf("?");
		if (questionMarkIdx != -1) {
			path = path.substring(0, questionMarkIdx);
		}
		this.path = path;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	private void checkQueryStringParameters(String path) {
		try {
			path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			Logger.log("Error decoding path '" + path + "'", e);
		}

		int questionMarkIdx = path.indexOf("?");
		if (questionMarkIdx != -1) {
			String queryString = path.substring(questionMarkIdx + 1);
			String[] parameters = queryString.split("&");

			for (String parameter : parameters) {
				String[] parameterSplitted = parameter.split("=");
				String key = parameterSplitted[0];
				String value = parameterSplitted[1];
				queryStringParamters.put(key, value);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(httpMethod).append(" ").append(path).append(" ").append(httpVersion).append(System.lineSeparator());

		if (!queryStringParamters.isEmpty()) {
			sb.append("Query string parameters: ").append(queryStringParamters).append(System.lineSeparator());
		}

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
		}

		if (!body.isBlank()) {
			sb.append(System.lineSeparator()).append(body);
		}

		return sb.toString();
	}

}

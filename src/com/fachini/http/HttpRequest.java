package com.fachini.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

	private Map<String, String> headers = new LinkedHashMap<>();
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
		this.path = path;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(httpMethod).append(" ").append(path).append(" ").append(httpVersion).append(System.lineSeparator());

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
		}

		if (!body.isBlank()) {
			sb.append("content-length").append(": ").append(body.length());
			sb.append(System.lineSeparator()).append(body);
		}

		return sb.toString();
	}

}

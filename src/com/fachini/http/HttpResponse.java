package com.fachini.http;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.fachini.http.utils.DateTimeUtils;

public abstract class HttpResponse {

	private HttpRequest request;

	private Map<String, String> headers = new HashMap<>();

	private HttpStatus httpStatus = HttpStatus.OK;

	public HttpResponse(HttpRequest request) {
		this.request = request;
		headers.put("Server", "Fachini HTTP Server");
		headers.put("Date", DateTimeUtils.HTTP_DATE_TIME_FORMATTER.format(LocalDateTime.now(ZoneOffset.UTC)));
		headers.put("Connection", "close");
	}

	private byte[] content = null;

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content.getBytes();
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public abstract void handle();

	public byte[] getData() {
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 ").append(httpStatus.getCode()).append(System.lineSeparator());

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
		}

		byte[] bytes = sb.toString().getBytes();

		if (content != null && content.length > 0) {
			bytes = concatenate(bytes, System.lineSeparator().getBytes());
			bytes = concatenate(bytes, content);
		}

		return bytes;
	}

	private byte[] concatenate(byte[] a, byte[] b) {
		int aLen = a.length;
		int bLen = b.length;

		byte[] c = new byte[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}
}

package com.fachini.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

	private Map<String, String> headers = new HashMap<>();

	private HttpStatus httpStatus = HttpStatus.OK;

	public HttpResponse() {
		headers.put("server", "Fachini HTTP Server");
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

	public void dealWithFile(String path) {
		Path filePath = getFilePath(path);

		if (!Files.exists(filePath)) {
			httpStatus = HttpStatus.NOT_FOUND;
			setContent("File not found");
		} else {
			try {
				content = Files.readAllBytes(filePath);
				headers.put("content-type", guessContentType(filePath));
				headers.put("content-length", Integer.toString(content.length));
			} catch (IOException e) {
				Logger.log("Error dealing with file " + path, e);

				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				setContent("Internal server error");
			}

		}
	}

	private String guessContentType(Path filePath) throws IOException {
		return Files.probeContentType(filePath);
	}

	private Path getFilePath(String path) {
		if ("/".equals(path)) {
			path = "/index.html";
		}

		return Paths.get(Server.SITE_PATH, path);
	}

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

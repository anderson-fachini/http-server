package com.fachini.http.responsehandlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fachini.http.HttpMethod;
import com.fachini.http.HttpRequest;
import com.fachini.http.HttpResponse;
import com.fachini.http.HttpStatus;
import com.fachini.http.Logger;
import com.fachini.http.Server;

public class FileHandler extends HttpResponse {

	public FileHandler(HttpRequest request) {
		super(request);
	}

	@Override
	public void handle() {
		String path = getRequest().getPath();
		Path filePath = getFilePath(path);

		if (!Files.exists(filePath)) {
			setHttpStatus(HttpStatus.NOT_FOUND);
			setContent("File not found");
		} else {
			byte[] content = new byte[0];
			try {
				content = Files.readAllBytes(filePath);
				addHeader("content-type", guessContentType(filePath));
				addHeader("content-length", Integer.toString(content.length));

				if (List.of(HttpMethod.GET, HttpMethod.POST).contains(getRequest().getHttpMethod())) {
					setContent(content);
				}
			} catch (IOException e) {
				Logger.log("Error dealing with file " + path, e);

				setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
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

}

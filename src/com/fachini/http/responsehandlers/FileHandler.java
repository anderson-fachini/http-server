package com.fachini.http.responsehandlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fachini.http.HttpMethod;
import com.fachini.http.HttpRequest;
import com.fachini.http.HttpResponse;
import com.fachini.http.HttpStatus;
import com.fachini.http.Logger;
import com.fachini.http.Server;

public class FileHandler extends HttpResponse {

	private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("EEE, d MMM yyyy kk:mm:ss 'GMT'");

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
				addHeader("Content-Type", guessContentType(filePath));
				addHeader("Content-Length", Integer.toString(content.length));

				LocalDateTime lastModifiedDate = getLastModified(filePath);
				if (lastModifiedDate != null) {
					addHeader("Last-Modified", DTF.format(lastModifiedDate));
				}

				boolean ifModifiedHeaderSet = setIfModifiedHeader(lastModifiedDate);

				if (!ifModifiedHeaderSet
						&& List.of(HttpMethod.GET, HttpMethod.POST).contains(getRequest().getHttpMethod())) {
					setContent(content);
				}
			} catch (IOException e) {
				Logger.log("Error dealing with file " + path, e);

				setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				setContent("Internal server error");
			}
		}
	}

	private boolean setIfModifiedHeader(LocalDateTime lastModifiedDate) {
		String modifyCheckHeader = getRequest().getHeader("If-Modified-Since");
		if (modifyCheckHeader == null) {
			modifyCheckHeader = getRequest().getHeader("If-Unmodified-Since");
		}

		if (modifyCheckHeader != null && !modifyCheckHeader.isBlank()) {
			LocalDateTime modifyCheckDateTime = LocalDateTime.parse(modifyCheckHeader, DTF);
			if (lastModifiedDate != null && (lastModifiedDate.isBefore(modifyCheckDateTime)
					|| lastModifiedDate.isEqual(modifyCheckDateTime))) {
				setHttpStatus(HttpStatus.NOT_MODIFIED);
				return true;
			}
		}

		return false;
	}

	private LocalDateTime getLastModified(Path path) {
		try {
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			FileTime lastModifiedTime = attr.lastModifiedTime();

			LocalDateTime lastModified = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneOffset.UTC);
			lastModified = lastModified.minusNanos(lastModified.getNano());
			return lastModified;
		} catch (IOException e) {
			Logger.log("Error checking modified date of file " + path, e);
			return null;
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

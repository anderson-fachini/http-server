package com.fachini.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fachini.http.responsehandlers.HttpResponseFactory;

public class Server {

	private static int PORT = 0;
	public static String SITE_PATH = "";

	static {
		String port = System.getenv("PORT");
		if (port != null && !port.isBlank()) {
			try {
				PORT = Integer.parseInt(port);
			} catch (NumberFormatException e) {
				// do nothing
			}
		} else {
			PORT = 8888;
		}

		String sitePath = System.getenv("SITE_PATH");
		if (sitePath != null && !sitePath.isBlank()) {
			Path path = Path.of(sitePath);
			if (Files.exists(path)) {
				SITE_PATH = sitePath;
			}
		}

		if (SITE_PATH.isBlank()) {
			throw new RuntimeException("SITE_PATH environment variable must be informed");
		}
	}

	public static void main(String[] args) {
		Logger.log("Starting the server on port " + PORT);

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			while (true) {
				try (Socket client = serverSocket.accept()) {
					handleClient(client);
				}
			}
		} catch (IOException e) {
			Logger.log("Error handling socket", e);
		}
	}

	private static void handleClient(Socket client) throws IOException {
		Logger.log("Debug: got new client " + client.toString());

		HttpRequest httpRequest = new HttpRequest(client.getInputStream());
		Logger.log(httpRequest);

		HttpResponse response = HttpResponseFactory.getResponseFor(httpRequest);
		response.handle();

		OutputStream clientOutput = client.getOutputStream();
		clientOutput.write(response.getData());
		clientOutput.flush();
		client.close();
	}

}

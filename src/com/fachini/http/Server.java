package com.fachini.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private static int PORT = 8888;
	public static String SITE_PATH = "C:\\programs\\nginx-1.19.4\\html\\indice_logosofia\\public";

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

		HttpResponse response = handleRequest(httpRequest);
		// response.setContent("isso é um teste");

		OutputStream clientOutput = client.getOutputStream();
		clientOutput.write(response.getData());
		clientOutput.flush();
		client.close();
	}

	private static HttpResponse handleRequest(HttpRequest request) {
		HttpResponse response = new HttpResponse();
		response.dealWithFile(request.getPath());

		return response;
	}
}

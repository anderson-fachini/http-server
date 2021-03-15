package com.fachini.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.fachini.http.responsehandlers.HttpResponseFactory;

public class Server {

	public static void main(String[] args) {
		Logger.log("Starting the server on port " + ConfigurationManager.PORT);

		try (ServerSocket serverSocket = new ServerSocket(ConfigurationManager.PORT)) {
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

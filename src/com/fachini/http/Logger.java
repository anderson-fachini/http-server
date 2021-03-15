package com.fachini.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class Logger {

	private Logger() {
	}

	public static void log(Object log) {
		System.out.println(LocalDateTime.now() + " - " + log + System.lineSeparator());
	}

	public static void log(String log, Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		String message = log + " - " + t.getCause() + System.lineSeparator() + sw.toString();

		log(message);
	}
}

package com.fachini.http;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationManager {

    public static final int PORT = getPortNumber();
    public static final String SITE_PATH = getSitePath();

    private static int getPortNumber() {
        String port = System.getenv("PORT");

        if (port != null && !port.isBlank()) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }

        return 8888;
    }

    private static String getSitePath() {
        String sitePath = System.getenv("SITE_PATH");

        if (sitePath != null && !sitePath.isBlank()) {
            Path path = Path.of(sitePath);
            if (Files.exists(path)) {
                return sitePath;
            }

            throw new RuntimeException("The path informed for SITE_PATH does not exist");
        }

        throw new RuntimeException("SITE_PATH environment variable must be informed");
    }
}

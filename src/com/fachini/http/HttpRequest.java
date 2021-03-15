package com.fachini.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private Map<String, String> headers = new LinkedHashMap<>();
    private Map<String, String> queryStringParamters = new LinkedHashMap<>();
    private HttpMethod httpMethod;
    private String path;
    private String body = "";
    private String httpVersion;

    public HttpRequest(InputStream inputStream) {
        boolean readMethod = false;
        boolean readAllHeaders = false;

        List<String> lines = getContentLines(inputStream);

        for (String line : lines) {
            if (!readMethod) {
                String[] lineSplitted = line.split(" ");

                Logger.log("Checking HTTP method " + lineSplitted[0]);
                setHttpMethod(HttpMethod.valueOf(lineSplitted[0]));
                setPath(lineSplitted[1]);
                checkQueryStringParameters(lineSplitted[1]);
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
                body = line;
            }
        }
    }

    private List<String> getContentLines(InputStream inputStream) {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        try {
            int bytesRead = 0;
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                sb.append(new String(buffer));

                if (bytesRead < bufferSize) {
                    break;
                }

                buffer = new byte[bufferSize];
            }

            String content = sb.toString();
            content = content.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
            String[] contentSlitted = content.split("\n");

            lines.addAll(Arrays.asList(contentSlitted));
        } catch (IOException e) {
            Logger.log("Error reading request input stream", e);
            throw new RuntimeException(e);
        }

        return lines;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String header) {
        return headers.get(header);
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
        int questionMarkIdx = path.indexOf("?");
        if (questionMarkIdx != -1) {
            path = path.substring(0, questionMarkIdx);
        }
        this.path = path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public Map<String, String> getQueryStringParamters() {
        return queryStringParamters;
    }

    public String getQueryStringParameter(String name) {
        return queryStringParamters.get(name);
    }

    private void checkQueryStringParameters(String path) {
        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            Logger.log("Error decoding path '" + path + "'", e);
        }

        int questionMarkIdx = path.indexOf("?");
        if (questionMarkIdx != -1) {
            String queryString = path.substring(questionMarkIdx + 1);
            String[] parameters = queryString.split("&");

            for (String parameter : parameters) {
                String[] parameterSplitted = parameter.split("=");
                String key = parameterSplitted[0];
                String value = parameterSplitted[1];
                queryStringParamters.put(key, value);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(httpMethod).append(" ").append(path).append(" ").append(httpVersion).append(System.lineSeparator());

        if (!queryStringParamters.isEmpty()) {
            sb.append("Query string parameters: ").append(queryStringParamters).append(System.lineSeparator());
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
        }

        if (!body.isBlank()) {
            sb.append(System.lineSeparator()).append(body);
        }

        return sb.toString();
    }

}

package com.fachini.http;

public enum HttpStatus {

    OK(200, "OK"), //
    NOT_MODIFIED(304, "Not Modified"), //
    NOT_FOUND(404, "Not Found"), //
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private int code;
    private String description;

    private HttpStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getFormatted() {
        return Integer.toString(code) + " " + description;
    }

}

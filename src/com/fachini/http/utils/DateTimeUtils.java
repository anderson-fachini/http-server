package com.fachini.http.utils;

import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final DateTimeFormatter HTTP_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy kk:mm:ss 'GMT'");
}

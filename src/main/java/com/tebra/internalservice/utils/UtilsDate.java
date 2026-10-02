package com.tebra.internalservice.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilsDate {

    public static LocalDateTime parseDate(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(date, formatter);
    }
}

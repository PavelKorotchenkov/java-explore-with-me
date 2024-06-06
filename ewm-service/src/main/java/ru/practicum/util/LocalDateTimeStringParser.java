package ru.practicum.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeStringParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseStringToLocalDateTime(String date) {
        return date != null ? LocalDateTime.parse(date, FORMATTER) : null;
    }

    public static String parseLocalDateTimeToString(LocalDateTime date) {
        return date.format(FORMATTER);
    }
}

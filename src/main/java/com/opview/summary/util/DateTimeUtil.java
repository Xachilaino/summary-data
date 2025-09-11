package com.opview.summary.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {

    // 常用格式：API & 資料庫 post_time 格式
    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private static final DateTimeFormatter ALT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //將字串轉換為 LocalDateTime
     
    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;

        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(dateTimeStr, ALT_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDateTime.parse(dateTimeStr); // fallback ISO
                } catch (DateTimeParseException e3) {
                    return null;
                }
            }
        }
    }

    //將 LocalDateTime 轉換為字串
     
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DEFAULT_FORMATTER);
    }
}
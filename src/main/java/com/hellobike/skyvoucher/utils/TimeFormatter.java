package com.hellobike.skyvoucher.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class TimeFormatter {
    private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String localDateTime(long timeMillis) {
        return dateformat.format(System.currentTimeMillis());
    }
}

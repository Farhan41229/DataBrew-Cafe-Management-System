package com.databrew.cafe.util;

public class StringUtils {

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String capitalize(String value) {
        if (isNullOrEmpty(value)) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}

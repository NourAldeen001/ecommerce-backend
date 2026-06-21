package com.ecommerce.ecommerce_backend.common.util;

public final class StringUtils {

    private StringUtils() {}

    public static String normalize(String value) {
        if(value == null) return null;
        String trimmed = value.trim();
        if(trimmed.isEmpty()) return trimmed;
        return trimmed.substring(0, 1).toUpperCase()
                + trimmed.substring(1).toLowerCase();
    }
}

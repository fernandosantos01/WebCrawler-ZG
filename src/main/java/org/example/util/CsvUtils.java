package org.example.util;

public final class CsvUtils {

    private CsvUtils() {
    }

    public static String normalizeCell(String value) {
        return value == null ? "" : value.trim().replace('\u00A0', ' ');
    }
}
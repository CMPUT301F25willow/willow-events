package com.example.willowevents;

public class ValidateData {
    public static boolean containsOnlyDigits(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return str.matches("\\d+");
    }

    public static boolean notNull(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }
}

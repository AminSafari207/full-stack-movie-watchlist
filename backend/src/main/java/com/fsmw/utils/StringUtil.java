package com.fsmw.utils;

public class StringUtil {
    private StringUtil() {
        throw new IllegalStateException("'StringUtil' cannot be instantiated.");
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

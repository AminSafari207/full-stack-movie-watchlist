package com.fsmw.utils;

import com.password4j.Password;

public class PasswordUtil {
    private PasswordUtil() {
        throw new IllegalStateException("'PasswordUtil' cannot be instantiated.");
    }

    public static String encode(String newPassword) {
        return Password.hash(newPassword)
                .addPepper()
                .withBcrypt()
                .getResult();
    }

    public static boolean validate(String rawPassword, String encodedPassword) {
        return Password.check(rawPassword, encodedPassword).withBcrypt();
    }
}

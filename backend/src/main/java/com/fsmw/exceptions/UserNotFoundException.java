package com.fsmw.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("User not found: id=" + userId);
    }

    public UserNotFoundException(Long userId, Throwable cause) {
        super("User not found: id=" + userId, cause);
    }
}

package com.fsmw.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(Long userId) {
        super("User already exists: id=" + userId);
    }

    public UserAlreadyExistsException(Long userId, Throwable cause) {
        super("User already exists: id=" + userId, cause);
    }
}


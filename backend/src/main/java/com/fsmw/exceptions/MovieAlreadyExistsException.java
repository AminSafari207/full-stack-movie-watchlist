package com.fsmw.exceptions;

public class MovieAlreadyExistsException extends RuntimeException {
    public MovieAlreadyExistsException(Long movieId) {
        super("Movie already exists: id=" + movieId);
    }

    public MovieAlreadyExistsException(Long movieId, Throwable cause) {
        super("Movie already exists: id=" + movieId, cause);
    }
}

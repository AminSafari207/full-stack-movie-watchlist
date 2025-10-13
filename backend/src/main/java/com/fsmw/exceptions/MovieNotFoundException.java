package com.fsmw.exceptions;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(Long movieId) {
        super("Movie not found: id=" + movieId);
    }

    public MovieNotFoundException(Long movieId, Throwable cause) {
        super("Movie not found: id=" + movieId, cause);
    }
}

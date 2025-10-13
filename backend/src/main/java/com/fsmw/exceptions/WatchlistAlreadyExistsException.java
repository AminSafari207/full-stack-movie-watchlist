package com.fsmw.exceptions;

public class WatchlistAlreadyExistsException extends RuntimeException {
    public WatchlistAlreadyExistsException(Long userId, Long movieId) {
        super("Watchlist entry already exists: userId=" + userId + ", movieId=" + movieId);
    }

    public WatchlistAlreadyExistsException(Long userId, Long movieId, Throwable cause) {
        super("Watchlist entry already exists: userId=" + userId + ", movieId=" + movieId, cause);
    }
}

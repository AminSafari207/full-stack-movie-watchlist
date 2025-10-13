package com.fsmw.exceptions;

public class WatchlistNotFoundException extends RuntimeException {
    public WatchlistNotFoundException(Long userId, Long movieId) {
        super("Watchlist entry not found: userId=" + userId + ", movieId=" + movieId);
    }

    public WatchlistNotFoundException(Long userId, Long movieId, Throwable cause) {
        super("Watchlist entry not found: userId=" + userId + ", movieId=" + movieId, cause);
    }
}

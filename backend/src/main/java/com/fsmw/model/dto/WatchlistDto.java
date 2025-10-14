package com.fsmw.model.dto;

import com.fsmw.model.watchlist.Watchlist;

public record WatchlistDto(Long id, Long userId, Long movieId) {
    public static WatchlistDto from(Watchlist w) {
        return new WatchlistDto(
                w.getId(),
                w.getUser().getId(),
                w.getMovie().getId()
        );
    }
}
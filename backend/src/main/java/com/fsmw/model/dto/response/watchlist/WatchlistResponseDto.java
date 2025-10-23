package com.fsmw.model.dto.response.watchlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.dto.response.movie.MovieResponseDto;
import com.fsmw.model.user.User;
import com.fsmw.model.watchlist.Watchlist;

import java.util.List;

public record WatchlistResponseDto(int count, List<MovieResponseDto> movies) {
    public static WatchlistResponseDto fromUser(User user, ObjectMapper mapper) {
        List<MovieResponseDto> movies = user.getWatchlist().stream()
                .map(Watchlist::getMovie)
                .map(m -> mapper.convertValue(m, MovieResponseDto.class))
                .toList();

        return new WatchlistResponseDto(movies.size(), movies);
    }
}

package com.fsmw.model.dto;

import com.fsmw.model.movie.Movie;

public record MovieDto(Long id, String title, String genre, Long duration) {
    public static MovieDto from(Movie movie) {
        return new MovieDto(movie.getId(), movie.getTitle(), movie.getGenre(), movie.getDuration());
    }
}

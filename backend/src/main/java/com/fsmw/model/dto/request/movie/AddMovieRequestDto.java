package com.fsmw.model.dto.request.movie;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsmw.model.movie.Movie;

import java.time.LocalDate;

public record AddMovieRequestDto(
        String title,
        String description,
        String genre,
        @JsonProperty("posterImage") String posterImageBase64,
        Long duration,
        Integer rating,
        LocalDate releaseDate
) {
}

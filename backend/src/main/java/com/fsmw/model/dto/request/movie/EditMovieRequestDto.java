package com.fsmw.model.dto.request.movie;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record EditMovieRequestDto(
        Long movieId,
        String title,
        String description,
        String genre,
        @JsonProperty("posterImage") String posterImageBase64,
        Long duration,
        int rating,
        LocalDate releaseDate
) {
}

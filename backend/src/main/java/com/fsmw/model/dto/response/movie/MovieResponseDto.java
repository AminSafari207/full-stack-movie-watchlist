package com.fsmw.model.dto.response.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsmw.model.dto.response.user.UserSafeResponseDto;
import com.fsmw.model.user.User;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieResponseDto(
        Long id,
        String title,
        String description,
        String genre,
        @JsonProperty("posterImage") String posterImageBase64,
        Long duration,
        LocalDate releaseDate,
        int rating
) {
}

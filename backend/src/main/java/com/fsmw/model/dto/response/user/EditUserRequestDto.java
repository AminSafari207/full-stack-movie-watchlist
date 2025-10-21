package com.fsmw.model.dto.response.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.utils.ObjectMapperProvider;

import java.io.IOException;

public record EditUserRequestDto(
        String username,
        String email,
        String currentPassword,
        String newPassword,
        String profileImageBase64
) {

    public static EditUserRequestDto fromJson(String json) throws IOException {
        ObjectMapper mapper = ObjectMapperProvider.get();
        return mapper.readValue(json, EditUserRequestDto.class);
    }
}

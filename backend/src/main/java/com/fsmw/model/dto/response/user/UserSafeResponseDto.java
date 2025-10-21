package com.fsmw.model.dto.response.user;

import com.fsmw.model.user.User;

public record UserSafeResponseDto(String username, String email, String profileImageBase64) {
    public static UserSafeResponseDto from(User user) {
        return new UserSafeResponseDto(user.getUsername(), user.getEmail(), user.getProfileImageBase64());
    }
}

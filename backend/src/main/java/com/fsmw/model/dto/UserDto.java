package com.fsmw.model.dto;

import com.fsmw.model.user.User;

public record UserDto(String username, String email, String profileImageBase64) {
    public static UserDto from(User user) {
        return new UserDto(user.getUsername(), user.getEmail(), user.getProfileImageBase64());
    }
}

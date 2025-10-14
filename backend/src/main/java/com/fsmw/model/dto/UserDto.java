package com.fsmw.model.dto;

import com.fsmw.model.user.User;

public record UserDto(Long id, String username, String email) {
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}

package com.fsmw.session;

import com.fsmw.model.auth.Role;

import java.time.Instant;
import java.util.Set;

public record SessionData(Long userId, Set<Role> roles, Instant expiry) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiry);
    }
}

package com.fsmw.session;

import com.fsmw.model.auth.Role;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {
    private static final ConcurrentHashMap<String, SessionData> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
        throw new IllegalArgumentException("'SessionManager' cannot be instantiated.");
    }

    public static String createSession(Long userId, Set<Role> roles) {
        String sessionId = UUID.randomUUID().toString();
        SessionData data = new SessionData(userId, roles, Instant.now().plus(Duration.ofHours(1)));

        sessions.put(sessionId, data);

        return sessionId;
    }

    public static Optional<SessionData> getSession(String sessionId) {
        SessionData data = sessions.get(sessionId);

        if (data == null || data.isExpired()) {
            invalidateSession(sessionId);
            return Optional.empty();
        }

        return Optional.of(data);
    }

    public static void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
    }
}

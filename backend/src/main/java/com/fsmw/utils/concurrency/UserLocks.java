package com.fsmw.utils.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class UserLocks {
    private static final Map<Long, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    private UserLocks() {
        throw new IllegalStateException("'UserLocks' cannot be instantiated.");
    }

    public static ReentrantLock of(Long userId) {
        return LOCKS.computeIfAbsent(userId, id -> new ReentrantLock(true)); // or new ReentrantLock() for non-fair
    }
}
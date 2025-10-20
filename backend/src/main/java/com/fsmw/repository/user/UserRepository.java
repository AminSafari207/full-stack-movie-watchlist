package com.fsmw.repository.user;

import com.fsmw.model.user.User;
import com.fsmw.repository.base.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

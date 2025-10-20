package com.fsmw.service.user;

import com.fsmw.model.user.User;
import com.fsmw.service.base.BaseService;

import java.util.Optional;

public interface UserService extends BaseService<User, Long> {
    Optional<User> findByEmail(String email);
}

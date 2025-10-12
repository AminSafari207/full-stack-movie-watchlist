package com.fsmw.repository.user;

import com.fsmw.model.user.User;
import com.fsmw.repository.base.AbstractBaseRepository;
import jakarta.persistence.EntityManager;

public class UserRepositoryImpl extends AbstractBaseRepository<User, Long> implements UserRepository {
    public UserRepositoryImpl(EntityManager em) {
        super(em, User.class);
    }
}

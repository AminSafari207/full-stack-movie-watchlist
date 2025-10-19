package com.fsmw.service.user;

import com.fsmw.model.user.User;
import com.fsmw.repository.user.UserRepository;
import com.fsmw.repository.user.UserRepositoryImpl;
import com.fsmw.service.base.AbstractBaseService;
import com.fsmw.service.base.BaseService;
import jakarta.persistence.EntityManagerFactory;

public class UserServiceImpl
        extends AbstractBaseService<User, Long, UserRepository>
        implements UserService {

    public UserServiceImpl(EntityManagerFactory emf) {
        super(emf, UserRepositoryImpl::new);
    }
}

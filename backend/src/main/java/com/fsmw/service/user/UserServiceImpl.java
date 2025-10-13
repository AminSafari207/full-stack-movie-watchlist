package com.fsmw.service.user;

import com.fsmw.model.user.User;
import com.fsmw.repository.user.UserRepository;
import com.fsmw.repository.user.UserRepositoryImpl;
import com.fsmw.service.base.AbstractBaseService;
import com.fsmw.service.base.BaseService;

public class UserServiceImpl
        extends AbstractBaseService<User, Long, UserRepository>
        implements UserService {

    public UserServiceImpl() {
        super(UserRepositoryImpl::new);
    }
}

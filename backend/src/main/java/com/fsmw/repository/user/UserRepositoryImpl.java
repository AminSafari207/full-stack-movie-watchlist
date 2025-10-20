package com.fsmw.repository.user;

import com.fsmw.model.user.User;
import com.fsmw.repository.base.AbstractBaseRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class UserRepositoryImpl extends AbstractBaseRepository<User, Long> implements UserRepository {
    public UserRepositoryImpl(EntityManager em) {
        super(em, User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return em.createQuery("select e from User e where e.email = :email", entityClassRef)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }
}

package com.fsmw.repository.auth;

import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.repository.base.AbstractBaseRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class RoleRepositoryImpl extends AbstractBaseRepository<Role, Long> implements RoleRepository {
    public RoleRepositoryImpl(EntityManager em) {
        super(em, Role.class);
    }

    @Override
    public Optional<Role> findByName(RoleType roleType) {
        return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                .setParameter("name", roleType)
                .getResultStream()
                .findFirst();
    }
}

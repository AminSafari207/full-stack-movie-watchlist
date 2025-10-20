package com.fsmw.service.auth;

import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.repository.auth.RoleRepository;
import com.fsmw.repository.auth.RoleRepositoryImpl;
import com.fsmw.service.base.AbstractBaseService;
import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

public class RoleServiceImpl
        extends AbstractBaseService<Role, Long, RoleRepository>
        implements RoleService {

    public RoleServiceImpl(EntityManagerFactory emf) {
        super(emf, RoleRepositoryImpl::new);
    }

    @Override
    public Optional<Role> findByName(RoleType roleType) {
        return executeTransaction(em -> coreRepository(em).findByName(roleType));
    }
}
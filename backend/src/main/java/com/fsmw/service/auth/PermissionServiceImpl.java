package com.fsmw.service.auth;

import com.fsmw.model.auth.Permission;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.repository.auth.PermissionRepository;
import com.fsmw.repository.auth.PermissionRepositoryImpl;
import com.fsmw.service.base.AbstractBaseService;
import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

public class PermissionServiceImpl
        extends AbstractBaseService<Permission, Long, PermissionRepository>
        implements PermissionService {

    public PermissionServiceImpl(EntityManagerFactory emf) {
        super(emf, PermissionRepositoryImpl::new);
    }

    @Override
    public Optional<Permission> findByName(PermissionType permissionType) {
        return executeTransaction(em -> coreRepository(em).findByName(permissionType));
    }
}

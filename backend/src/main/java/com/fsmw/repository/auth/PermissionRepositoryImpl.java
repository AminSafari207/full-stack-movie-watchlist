package com.fsmw.repository.auth;

import com.fsmw.model.user.rnp.Permission;
import com.fsmw.model.user.rnp.PermissionType;
import com.fsmw.repository.base.AbstractBaseRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class PermissionRepositoryImpl extends AbstractBaseRepository<Permission, Long> implements PermissionRepository {
    public PermissionRepositoryImpl(EntityManager em) {
        super(em, Permission.class);
    }

    @Override
    public Optional<Permission> findByName(PermissionType permissionType) {
        return em.createQuery("SELECT p FROM Permission p WHERE p.name = :name", Permission.class)
                .setParameter("name", permissionType)
                .getResultStream()
                .findFirst();
    }
}
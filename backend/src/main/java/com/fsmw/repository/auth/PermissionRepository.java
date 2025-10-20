package com.fsmw.repository.auth;

import com.fsmw.model.user.rnp.Permission;
import com.fsmw.model.user.rnp.PermissionType;
import com.fsmw.repository.base.BaseRepository;

import java.util.Optional;

public interface PermissionRepository extends BaseRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionType permissionType);
}

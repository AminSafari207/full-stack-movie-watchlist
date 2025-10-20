package com.fsmw.service.auth;

import com.fsmw.model.auth.Permission;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.service.base.BaseService;

import java.util.Optional;

public interface PermissionService extends BaseService<Permission, Long> {
    Optional<Permission> findByName(PermissionType permissionType);
}

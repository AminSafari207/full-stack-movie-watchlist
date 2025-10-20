package com.fsmw.service.auth;

import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.service.base.BaseService;

import java.util.Optional;

public interface RoleService extends BaseService<Role, Long> {
    Optional<Role> findByName(RoleType roleType);
}
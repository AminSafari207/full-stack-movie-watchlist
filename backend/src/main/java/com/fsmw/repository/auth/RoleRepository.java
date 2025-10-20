package com.fsmw.repository.auth;

import com.fsmw.model.user.rnp.Role;
import com.fsmw.model.user.rnp.RoleType;
import com.fsmw.repository.base.BaseRepository;

import java.util.Optional;

public interface RoleRepository extends BaseRepository<Role, Long> {
    Optional<Role> findByName(RoleType roleType);
}


package com.fsmw.service;

import com.fsmw.model.auth.Permission;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.repository.auth.PermissionRepository;
import com.fsmw.repository.auth.PermissionRepositoryImpl;
import com.fsmw.repository.auth.RoleRepository;
import com.fsmw.repository.auth.RoleRepositoryImpl;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.stream.Collectors;

public class RolePermissionSeeder {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionSeeder(EntityManager em) {
        this.roleRepository = new RoleRepositoryImpl(em);
        this.permissionRepository = new PermissionRepositoryImpl(em);
    }

    private static final Map<RoleType, Set<PermissionType>> ROLE_PERMISSION_MAP = Map.of(
            RoleType.USER, Set.of(
                    PermissionType.CAN_VIEW_WATCHLIST,
                    PermissionType.CAN_EDIT_PROFILE
            ),

            RoleType.ADMIN, Set.of(
                    PermissionType.values()
            )
    );

    public void seed() {
        Arrays.stream(PermissionType.values())
                .forEach(p -> permissionRepository
                        .findByName(p)
                        .orElseGet(() -> permissionRepository.save(Permission.builder().name(p).build()))
                );


        for (var entry : ROLE_PERMISSION_MAP.entrySet()) {
            RoleType roleType = entry.getKey();
            Set<PermissionType> permissionTypes = entry.getValue();

            Role role = roleRepository
                    .findByName(roleType)
                    .orElseGet(() -> roleRepository.save(Role.builder().name(roleType).build()));

            List<Permission> allPermissions = permissionRepository.findAll();
            Set<Permission> assignedPerms = allPermissions.stream()
                    .filter(p -> permissionTypes.contains(p.getName()))
                    .collect(Collectors.toSet());

            role.setPermissions(assignedPerms);
            roleRepository.save(role);
        }
    }
}


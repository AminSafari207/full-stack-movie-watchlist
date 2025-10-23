package com.fsmw.service;

import com.fsmw.config.PersistenceUnit;
import com.fsmw.model.auth.Permission;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.service.auth.PermissionService;
import com.fsmw.service.auth.RoleService;

import java.util.*;
import java.util.stream.Collectors;

public class RolePermissionSeeder {
    private final RoleService roleService;
    private final PermissionService permissionService;

    public RolePermissionSeeder(PersistenceUnit persistenceUnit) {
        ServiceProvider serviceProvider = new ServiceProvider(persistenceUnit);

        this.roleService = serviceProvider.getRoleService();
        this.permissionService = serviceProvider.getPermissionService();
    }

    private final Map<RoleType, Set<PermissionType>> ROLE_PERMISSION_MAP = Map.of(
            RoleType.USER, Set.of(
                    PermissionType.CAN_VIEW_PROFILE,
                    PermissionType.CAN_EDIT_PROFILE,
                    PermissionType.CAN_VIEW_MOVIE,
                    PermissionType.CAN_VIEW_WATCHLIST
            ),

            RoleType.ADMIN, Set.of(
                    PermissionType.values()
            )
    );

    public void seed() {
        Arrays.stream(PermissionType.values())
                .forEach(p -> permissionService
                        .findByName(p)
                        .orElseGet(() -> permissionService.save(
                                Permission.builder().name(p).build()
                        ))
                );

        for (var entry : ROLE_PERMISSION_MAP.entrySet()) {
            RoleType roleType = entry.getKey();
            Set<PermissionType> permissionTypes = entry.getValue();

            Role role = roleService
                    .findByName(roleType)
                    .orElseGet(() -> roleService.save(
                            Role.builder().name(roleType).build()
                    ));

            List<Permission> allPermissions = permissionService.findAll();
            Set<Permission> assignedPerms = allPermissions.stream()
                    .filter(p -> permissionTypes.contains(p.getName()))
                    .collect(Collectors.toSet());

            role.setPermissions(assignedPerms);
            roleService.save(role);
        }
    }
}


package com.fsmw.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.config.PersistenceUnit;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.service.ServiceProvider;
import com.fsmw.utils.ServletResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

public class AuthorizationServiceImpl implements AuthorizationService {
    private Set<Role> extractRoles(HttpServletRequest req) {
        Object rolesAttr = req.getAttribute("roles");

        if (rolesAttr instanceof Set<?>) {
            return (Set<Role>) rolesAttr;
        }

        return Set.of();
    }

    @Override
    public boolean hasRole(HttpServletRequest req, RoleType role) {
        return extractRoles(req).stream()
                .anyMatch(r -> r.getName() == role);
    }

    @Override
    public boolean hasPermission(HttpServletRequest req, PermissionType permission) {
        return extractRoles(req).stream()
                .flatMap(r -> r.getPermissions().stream())
                .anyMatch(p -> p.getName() == permission);
    }

    @Override
    public boolean requireRole(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, RoleType role) throws IOException {
        if (!hasRole(req, role)) {
            resp.setContentType("application/text");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_FORBIDDEN,
                    "access denied: requires '" + role + "' role",
                    "You are not authorized to perform this action"
            );

            return false;
        }

        return true;
    }

    @Override
    public boolean requirePermission(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, PermissionType permission) throws IOException {
        if (!hasPermission(req, permission)) {
            resp.setContentType("application/text");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_FORBIDDEN,
                    "access denied: requires '" + permission + "' permission",
                    "You are not authorized to perform this action"
            );

            return false;
        }

        return true;
    }
}

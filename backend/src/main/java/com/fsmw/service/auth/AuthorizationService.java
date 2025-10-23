package com.fsmw.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.model.auth.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthorizationService {
    boolean hasRole(HttpServletRequest req, RoleType role);
    boolean hasPermission(HttpServletRequest req, PermissionType permission);

    boolean requireRole(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, RoleType role) throws IOException;
    boolean requirePermission(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, PermissionType permission) throws IOException;
}

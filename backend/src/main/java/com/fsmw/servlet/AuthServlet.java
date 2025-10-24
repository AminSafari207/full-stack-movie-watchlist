package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import com.fsmw.model.dto.response.user.UserSafeResponseDto;
import com.fsmw.model.user.User;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.RoleService;
import com.fsmw.service.user.UserService;
import com.fsmw.servlet.base.BaseServlet;
import com.fsmw.session.SessionManager;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.PasswordUtil;
import com.fsmw.utils.ServletResponseUtil;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.Set;

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {
    private final ObjectMapper mapper = ObjectMapperProvider.get();;
    private UserService userService;
    private RoleService roleService;

    @Override
    public void init() {
        ServiceProvider sp = new ServiceProvider();

        this.userService = sp.getUserService();
        this.roleService = sp.getRoleService();

        registerPost("/register", this::handleRegister);
        registerPost("/login", this::handleLogin);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            Optional<String> username = ServletUtil.requireBlanksOrNullString(req, resp, "username");
            if (username.isEmpty()) return;

            Optional<String> email    = ServletUtil.requireBlanksOrNullString(req, resp, "email");
            if (email.isEmpty()) return;

            Optional<String> password = ServletUtil.requireBlanksOrNullString(req, resp, "password");
            if (password.isEmpty()) return;

            String encodedPassword = PasswordUtil.encode(password.get());
            Optional<Role> userRole = roleService.findByName(RoleType.USER);

            if (userRole.isEmpty()) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "USER role not found",
                        "Registration is temporarily unavailable"
                );
                return;
            }

            Optional<User> foundUserOpt = userService.findByEmail(email.get());

            if (foundUserOpt.isPresent()) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "user is already registered",
                        "Email is already in use"
                );
                return;
            }

            User user = User.builder()
                    .username(username.get())
                    .email(email.get())
                    .password(encodedPassword)
                    .roles(Set.of(userRole.get()))
                    .build();

            User saved = userService.save(user);
            UserSafeResponseDto userSafeResponseDto = UserSafeResponseDto.from(saved);

            resp.setStatus(HttpServletResponse.SC_CREATED);

            ServletResponseUtil.writeSuccess(
                    resp,
                    HttpServletResponse.SC_CREATED,
                    "",
                    "User registered successfully",
                    userSafeResponseDto
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            Optional<String> username = ServletUtil.requireBlanksOrNullString(req, resp, "username");
            if (username.isEmpty()) return;

            Optional<String> password = ServletUtil.requireBlanksOrNullString(req, resp, "password");
            if (password.isEmpty()) return;

            Optional<User> userOpt = userService.findByUsername(username.get());

            if (userOpt.isEmpty() || !PasswordUtil.validate(password.get(), userOpt.get().getPassword())) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "username or password not found",
                        "Username or password is not incorrect"
                );
                return;
            }

            User foundUser = userOpt.get();
            String sessionId = SessionManager.createSession(foundUser.getId(), foundUser.getRoles());

            Cookie cookie = new Cookie("JSESSIONID", sessionId);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");

            resp.addCookie(cookie);

            UserSafeResponseDto userSafeResponseDto = UserSafeResponseDto.from(foundUser);

            ServletResponseUtil.writeSuccess(
                    resp,
                    HttpServletResponse.SC_OK,
                    "",
                    "Login successful",
                    userSafeResponseDto
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }
}

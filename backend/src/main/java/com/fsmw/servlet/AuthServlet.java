package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.model.dto.ApiResponseDto;
import com.fsmw.model.dto.UserDto;
import com.fsmw.model.user.User;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.RoleService;
import com.fsmw.service.user.UserService;
import com.fsmw.servlet.base.BaseServlet;
import com.fsmw.session.SessionManager;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.PasswordUtil;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@WebServlet("/auth/*")
public class AuthServlet extends BaseServlet {
    private final ObjectMapper mapper = ObjectMapperProvider.get();;
    private UserService userService;
    private RoleService roleService;

    @Override
    public void init() {
        ServiceProvider serviceProvider = new ServiceProvider();

        this.userService = serviceProvider.getUserService();
        this.roleService = serviceProvider.getRoleService();

        registerPost("/register", this::handleRegister);
        registerPost("/login", this::handleLogin);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            String username = ServletUtil.validateBlanksOrNullString(req, resp, mapper, "username");
            String email    = ServletUtil.validateBlanksOrNullString(req, resp, mapper, "email");
            String password = ServletUtil.validateBlanksOrNullString(req, resp, mapper, "password");

            password = PasswordUtil.encode(password);

            Optional<Role> userRole = roleService.findByName(RoleType.USER);

            if (userRole.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                mapper.writeValue(
                        resp.getWriter(),
                        ApiResponseDto.error(
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "USER role not found",
                                "Registration is temporarily unavailable"
                        )
                );

                return;
            }

            Optional<User> foundUserOpt = userService.findByEmail(email);

            if (foundUserOpt.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                mapper.writeValue(
                        resp.getWriter(),
                        ApiResponseDto.error(
                                HttpServletResponse.SC_BAD_REQUEST,
                                "user is already registered",
                                "Email is already in use"
                        )
                );

                return;
            }

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .roles(Set.of(userRole.get()))
                    .build();

            User saved = userService.save(user);
            UserDto userDto = new UserDto(saved.getId(), saved.getUsername(), saved.getEmail());

            resp.setStatus(HttpServletResponse.SC_CREATED);

            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.success(
                            HttpServletResponse.SC_CREATED,
                            "",
                            "User registered successfully",
                            userDto
                    )
            );
        } catch (ServletUtil.ValidationException ignored) {
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            String username = ServletUtil.validateBlanksOrNullString(req, resp, mapper, "username");
            String password = ServletUtil.validateBlanksOrNullString(req, resp, mapper, "password");

            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty() || !PasswordUtil.validate(password, userOpt.get().getPassword())) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                mapper.writeValue(
                        resp.getWriter(),
                        ApiResponseDto.error(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "username or password not found",
                                "Username or password is not incorrect"
                        )
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

            UserDto userDto = new UserDto(
                    foundUser.getId(),
                    foundUser.getUsername(),
                    foundUser.getEmail()
            );

            resp.setStatus(HttpServletResponse.SC_OK);

            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.success(
                            HttpServletResponse.SC_OK,
                            "",
                            "Login successful",
                            userDto
                    )
            );
        } catch (ServletUtil.ValidationException ignored) {
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }
}

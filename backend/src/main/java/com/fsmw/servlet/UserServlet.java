package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.exceptions.UserNotFoundException;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import com.fsmw.model.dto.response.user.EditUserRequestDto;
import com.fsmw.model.dto.response.user.UserSafeResponseDto;
import com.fsmw.model.user.User;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.user.UserService;
import com.fsmw.servlet.base.BaseServlet;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.PasswordUtil;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private final ObjectMapper mapper = ObjectMapperProvider.get();
    private UserService userService;

    @Override
    public void init() {
        ServiceProvider serviceProvider = new ServiceProvider();

        this.userService = serviceProvider.getUserService();

        registerPost("/getprofile", this::handleGetUserProfile);
        registerPost("/editprofile", this::handleEditUserProfile);
    }

    private void handleGetUserProfile(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            Long userId = (Long) req.getAttribute("userId");
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(
                        resp.getWriter(),
                        ApiResponseDto.error(
                                HttpServletResponse.SC_NOT_FOUND,
                                "user not found",
                                "User with ID '" + userId + "' not found"
                        )
                );
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.success(
                            HttpServletResponse.SC_OK,
                            "",
                            "User found successfully",
                            UserSafeResponseDto.from(userOpt.get())
                    )
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

    private void handleEditUserProfile(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            String body = ServletUtil.readRequestBody(req);
            EditUserRequestDto eurDto = EditUserRequestDto.fromJson(body);

            Long userId = (Long) req.getAttribute("userId");
            User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            // TODO: how to prevent duplicate not found user handler?

            if (eurDto.username() != null) {
                if (!eurDto.username().equals(user.getUsername())) {
                    if (userService.findByUsername(eurDto.username()).isPresent()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        mapper.writeValue(
                                resp.getWriter(),
                                ApiResponseDto.error(
                                        HttpServletResponse.SC_BAD_REQUEST,
                                        "username is already in use",
                                        "Username is already in use"
                                )
                        );
                        return;
                    }

                    user.setUsername(eurDto.username());
                }
            }

            if (eurDto.email() != null) {
                if (!eurDto.email().equals(user.getEmail())) {
                    if (userService.findByEmail(eurDto.email()).isPresent()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        mapper.writeValue(
                                resp.getWriter(),
                                ApiResponseDto.error(
                                        HttpServletResponse.SC_BAD_REQUEST,
                                        "email is already in use",
                                        "Email is already in use"
                                )
                        );
                        return;
                    }

                    user.setEmail(eurDto.email());
                }
            }

            if (eurDto.currentPassword() != null || eurDto.newPassword() != null) {
                if (eurDto.currentPassword() == null || eurDto.newPassword() == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    mapper.writeValue(
                            resp.getWriter(),
                            ApiResponseDto.error(
                                    HttpServletResponse.SC_BAD_REQUEST,
                                    "both current and new password must be provided",
                                    "Both current and new password must be provided"
                            )
                    );
                    return;
                }

                if (!PasswordUtil.validate(eurDto.currentPassword(), user.getPassword())) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    mapper.writeValue(
                            resp.getWriter(),
                            ApiResponseDto.error(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    "current password is invalid",
                                    "Current password is wrong"
                            )
                    );
                    return;
                }

                user.setPassword(PasswordUtil.encode(eurDto.newPassword()));
            }

            if (eurDto.profileImageBase64() != null) {
                user.setProfileImageBase64(eurDto.profileImageBase64());
            }

            userService.save(user);

            UserSafeResponseDto respDto = UserSafeResponseDto.from(user);

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.success(
                            HttpServletResponse.SC_OK,
                            "",
                            "Profile updated successfully",
                            respDto
                    )
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }
}
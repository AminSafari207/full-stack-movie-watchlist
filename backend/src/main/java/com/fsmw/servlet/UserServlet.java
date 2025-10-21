package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.dto.ApiResponseDto;
import com.fsmw.model.dto.CreateUserDto;
import com.fsmw.model.dto.ErrorDto;
import com.fsmw.model.dto.UserDto;
import com.fsmw.model.user.User;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.RoleService;
import com.fsmw.service.user.UserService;
import com.fsmw.servlet.base.BaseServlet;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private final ObjectMapper mapper = ObjectMapperProvider.get();
    private UserService userService;

    @Override
    public void init() {
        ServiceProvider serviceProvider = new ServiceProvider();

        this.userService = serviceProvider.getUserService();

        registerPost("/getprofile", this::handleGetUserProfile);
//        registerPost("/editprofile", this::handleEditUserProfile);
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
                            UserDto.from(userOpt.get())
                    )
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        try {
//            String idParam = req.getParameter("id");
//
//            if (idParam != null) {
//                Long id = Long.valueOf(idParam);
//                Optional<User> userOpt = userService.findById(id);
//
//                if (userOpt.isPresent()) {
//                    writeJson(resp, UserDto.from(userOpt.get()));
//                } else {
//                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                }
//            } else {
//                List<User> users = userService.findAll();
//                List<UserDto> dtos = users.stream()
//                        .map(UserDto::from)
//                        .collect(Collectors.toList());
//
//                writeJson(resp, dtos);
//            }
//        } catch (Exception e) {
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            writeJson(resp, new ErrorDto("Server error", e.getMessage()));
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        try {
//            CreateUserDto dto = mapper.readValue(req.getInputStream(), CreateUserDto.class);
//
//            User user = User.builder()
//                    .username(dto.username())
//                    .email(dto.email())
//                    .password(dto.password())
//                    .build();
//
//            User saved = userService.save(user);
//
//            resp.setStatus(HttpServletResponse.SC_CREATED);
//            writeJson(resp, UserDto.from(saved));
//        } catch (Exception e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            writeJson(resp, new ErrorDto("Invalid request", e.getMessage()));
//        }
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        String idParam = req.getParameter("id");
//
//        if (idParam == null) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            writeJson(resp, new ErrorDto("Missing parameter", "id is required"));
//            return;
//        }
//
//        try {
//            Long id = Long.valueOf(idParam);
//            boolean deleted = userService.deleteById(id);
//
//            if (deleted) {
//                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//            } else {
//                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                writeJson(resp, new ErrorDto("Not found", "User with id " + id + " not found"));
//            }
//        } catch (NumberFormatException e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            writeJson(resp, new ErrorDto("Invalid id", "id must be a number"));
//        }
//    }
//
//    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
//        mapper.writeValue(resp.getOutputStream(), data);
//    }
}

package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.model.dto.ApiResponseDto;
import com.fsmw.model.dto.UserDto;
import com.fsmw.model.user.User;
import com.fsmw.repository.user.UserRepository;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.RoleService;
import com.fsmw.service.user.UserService;
import com.fsmw.service.watchlist.WatchlistService;
import com.fsmw.utils.PasswordUtil;
import com.fsmw.utils.validators.ServletRequestValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();;
    private UserService userService;
    private RoleService roleService;

    @Override
    public void init() {
        ServiceProvider serviceProvider = new ServiceProvider();

        this.userService = serviceProvider.getUserService();
        this.roleService = serviceProvider.getRoleService();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");

        try {
            String username = ServletRequestValidator.validateBlanksOrNullString(req, resp, mapper, "username");
            String email    = ServletRequestValidator.validateBlanksOrNullString(req, resp, mapper, "email");
            String password = ServletRequestValidator.validateBlanksOrNullString(req, resp, mapper, "password");

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
                            "User registered successfuly",
                            userDto
                    )
            );
        } catch (ServletRequestValidator.ValidationException ignored) {
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try {
                mapper.writeValue(resp.getWriter(),
                        ApiResponseDto.error(
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                e.getMessage(),
                                "Something went wrong"
                        )
                );
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
    }
}

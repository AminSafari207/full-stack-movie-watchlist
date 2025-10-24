package com.fsmw.servlet.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.config.PersistenceUnit;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.user.UserService;
import com.fsmw.session.SessionData;
import com.fsmw.session.SessionManager;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.ServletResponseUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private final ObjectMapper mapper = ObjectMapperProvider.get();
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) {
        ServiceProvider sp = new ServiceProvider(PersistenceUnit.MW);

        this.userService = sp.getUserService();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String sessionId = Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "JSESSIONID".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (sessionId == null) {
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "session not found in cookie",
                    "Your session is timed out"
            );
            return;
        }

        Optional<SessionData> session = SessionManager.getSession(sessionId);

        if (session.isEmpty()) {
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "session not found in session manager",
                    "Your session is timed out"
            );
            return;
        }

        if (!userService.existsById(session.get().userId())) {
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "user not found",
                    "User not found"
            );
            return;
        }

        req.setAttribute("userId", session.get().userId());
        req.setAttribute("roles", session.get().roles());

        chain.doFilter(request, response);
    }
}


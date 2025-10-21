package com.fsmw.servlet.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.dto.ApiResponseDto;
import com.fsmw.session.SessionData;
import com.fsmw.session.SessionManager;
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
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();

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
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.error(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "session not found in cookie",
                            "Your session is timed out"
                    )
            );

            return;
        }

        Optional<SessionData> session = SessionManager.getSession(sessionId);

        if (session.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.error(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "session not found in session manager",
                            "Your session is timed out"
                    )
            );

            return;
        }

        req.setAttribute("userId", session.get().userId());
        req.setAttribute("roles", session.get().roles());

        chain.doFilter(request, response);
    }
}


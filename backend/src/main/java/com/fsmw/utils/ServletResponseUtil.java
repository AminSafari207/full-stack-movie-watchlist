package com.fsmw.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletResponseUtil {
    private static final ObjectMapper mapper = ObjectMapperProvider.get();

    public static void writeError(HttpServletResponse resp, int status, String devMessage, String userMessage) {
        try {
            resp.setContentType("application/json");
            resp.setStatus(status);
            mapper.writeValue(resp.getWriter(), ApiResponseDto.error(status, devMessage, userMessage));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write error response", e);
        }
    }

    public static <T> void writeSuccess(HttpServletResponse resp, int status, String devMessage, String userMessage, T data) {
        try {
            resp.setContentType("application/json");
            resp.setStatus(status);
            mapper.writeValue(resp.getWriter(), ApiResponseDto.success(status, devMessage, userMessage, data));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write success response", e);
        }
    }

    public static <T> void writeSuccess(HttpServletResponse resp, T data) {
        writeSuccess(resp, HttpServletResponse.SC_OK, "", "", data);
    }

    public static void writeSuccess(HttpServletResponse resp) {
        writeSuccess(resp, new Object());
    }
}


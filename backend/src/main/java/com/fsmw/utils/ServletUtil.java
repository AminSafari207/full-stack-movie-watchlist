package com.fsmw.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.dto.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtil {
    private ServletUtil() {
        throw new IllegalStateException("'ServletRequestValidator' cannot be instantiated.");
    }

    public static String validateBlanksOrNullString(
            HttpServletRequest req,
            HttpServletResponse resp,
            ObjectMapper mapper,
            String paramName
    ) throws IOException {
        String value = req.getParameter(paramName);

        if (value == null || value.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            ApiResponseDto<Void> error = ApiResponseDto.error(
                    HttpServletResponse.SC_BAD_REQUEST,
                    paramName + " is null or blank",
                    StringUtil.capitalize(paramName) + " is required"
            );

            mapper.writeValue(resp.getWriter(), error);

            throw new ValidationException(paramName + " is invalid");
        }

        return value;
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static void handleCommonInternalException(
            HttpServletResponse resp,
            ObjectMapper mapper,
            Exception e
    ) {
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

    public static void handleCommonInvalidPath(
            HttpServletResponse resp,
            ObjectMapper mapper,
            String path
    ) {
        resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);

        try {
            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.error(
                            HttpServletResponse.SC_BAD_GATEWAY,
                            "invalid endpoint: " + path,
                            "Invalid request endpoint"
                    )
            );
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}

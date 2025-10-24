package com.fsmw.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class ServletUtil {
    private ServletUtil() {
        throw new IllegalStateException("'ServletRequestValidator' cannot be instantiated.");
    }

    public static Optional<String> requireBlanksOrNullString(
            HttpServletRequest req,
            HttpServletResponse resp,
            String paramName
    ) {
        String value = req.getParameter(paramName);

        if (value == null || value.isBlank()) {
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_BAD_REQUEST,
                    paramName + " is null or blank",
                    StringUtil.capitalize(paramName) + " is required"
            );

            return Optional.empty();
        }

        return Optional.of(value);
    }

    public static Long getRequiredLongParam(
            HttpServletRequest req,
            HttpServletResponse resp,
            String paramName
    ) throws IOException {
        String raw = req.getParameter(paramName);

        if (raw == null || raw.isBlank()) {
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "missing required parameter: " + paramName,
                    "Parameter '" + paramName + "' is required"
            );
            return null;
        }

        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            ServletResponseUtil.writeError(
                    resp,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "invalid number format for parameter: " + paramName,
                    "Parameter '" + paramName + "' must be a valid number"
            );
            return null;
        }
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

    public static String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = req.getReader()) {
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        return sb.toString();
    }
}

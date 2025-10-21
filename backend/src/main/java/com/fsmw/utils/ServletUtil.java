package com.fsmw.utils.validators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.model.dto.ApiResponseDto;
import com.fsmw.utils.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletRequestValidator  {
    private ServletRequestValidator() {
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
}

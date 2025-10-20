package com.fsmw.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private MetaStatus _meta;
    private Record<T> record;

    public enum MetaStatus {
        SUCCESS,
        ERROR
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Record<T> {
        private int errorCode;

        @Builder.Default
        private String devMessage = "";

        @Builder.Default
        private String userMessage = "";

        private T data;
    }

    public static <T> ApiResponseDto<T> success(int code, String devMessage, String userMessage, T data) {
        return new ApiResponseDto<>(
                MetaStatus.SUCCESS,
                new Record<>(code, devMessage, userMessage, data)
        );
    }

    public static <T> ApiResponseDto<T> error(int code, String devMessage, String userMessage) {
        return new ApiResponseDto<>(
                MetaStatus.ERROR,
                new Record<>(code, devMessage, userMessage, null)
        );
    }
}
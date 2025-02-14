package com.musinsa.assignment.product.dto.response;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorInfo error;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private int status;
        private String message;
        private Object details;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(new ErrorInfo(status, message, null))
            .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, T details) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(new ErrorInfo(status, message, details))
            .build();
    }
} 
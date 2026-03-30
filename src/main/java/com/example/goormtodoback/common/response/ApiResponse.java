package com.example.goormtodoback.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private final int status;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> fail(int status, String code, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

}

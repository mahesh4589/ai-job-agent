package com.ai.agent.dto;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ApiResponse<T> {

    private boolean success;
    String message;
    T data;
    int statusCode;

    public static <T> ApiResponse<T> success(String message, T data, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(statusCode)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .build();
    }



}

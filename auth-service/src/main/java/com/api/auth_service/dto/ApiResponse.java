package com.api.auth_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;
    int statusCode;

    public static <T> ApiResponse<T> success(String m, T d, int s) {
        return new ApiResponse<>(true, m, d, s);
    }

    public static <T> ApiResponse<T> error(String m, int s) {
        return new ApiResponse<>(false, m, null, s);
    }
}

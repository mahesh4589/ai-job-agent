package com.api.auth_service.config;

import com.api.auth_service.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Invalid request");
        return org.springframework.http.ResponseEntity.badRequest()
                .body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public org.springframework.http.ResponseEntity<ApiResponse<Void>> business(RuntimeException ex) {
        return org.springframework.http.ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }
}

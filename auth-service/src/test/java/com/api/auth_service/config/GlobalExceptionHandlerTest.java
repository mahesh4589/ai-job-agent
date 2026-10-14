package com.api.auth_service.config;

import com.api.auth_service.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void unsupportedHttpMethodReturns405InsteadOfInternalServerError() {
        HttpRequestMethodNotSupportedException exception =
                new HttpRequestMethodNotSupportedException("GET");

        var response = handler.methodNotSupported(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertEquals(405, body.getStatusCode());
        assertFalse(body.isSuccess());
        assertEquals("HTTP method is not allowed for this endpoint", body.getMessage());
    }
}

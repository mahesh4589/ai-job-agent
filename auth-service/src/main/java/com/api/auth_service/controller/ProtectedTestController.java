package com.api.auth_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Protected examples", description = "Endpoints requiring an access JWT")
@SecurityRequirement(name = "bearerAuth")
public class ProtectedTestController {

    @Operation(summary = "Check authenticated access", description = "Returns the authenticated principal. Supply the access JWT using the Authorize button.")
    @GetMapping("/secure")
    public String secure(Authentication authentication) {
        return "Authenticated as: " + authentication.getName();
    }
}

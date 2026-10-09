package com.api.auth_service.controller;
import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/test") public class ProtectedTestController { @GetMapping("/secure") public String secure(Authentication a){return "Authenticated as: "+a.getName();} }

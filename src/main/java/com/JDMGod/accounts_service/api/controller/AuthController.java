package com.JDMGod.accounts_service.api.controller;

import com.JDMGod.accounts_service.api.dto.AccountCreateRequest;
import com.JDMGod.accounts_service.api.dto.AuthResponse;
import com.JDMGod.accounts_service.api.dto.LoginRequest;
import com.JDMGod.accounts_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AccountCreateRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String token) {
        AuthResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }
}

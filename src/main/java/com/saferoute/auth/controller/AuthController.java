package com.saferoute.auth.controller;

import com.saferoute.auth.adapter.AuthAdapter;
import com.saferoute.common.dto.auth.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller.
 * Handles login, register, refresh token, logout and current user.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthAdapter authAdapter;

    /**
     * POST /api/v1/auth/login
     * Authenticate user and return tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        log.info("Login attempt for email: {}", request.email());
        AuthResponse response = authAdapter.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/register
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        log.info("Registration attempt for email: {}", request.email());
        AuthResponse response = authAdapter.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/refresh
     * Refresh access token using refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh attempt");
        AuthResponse response = authAdapter.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     * Logout user (invalidate tokens).
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("Logout request");
        authAdapter.logout();
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/auth/me
     * Get current authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        log.info("Get current user request");
        AuthResponse response = authAdapter.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}

package com.saferoute.auth.controller;

import com.saferoute.auth.adapter.AuthAdapter;
import com.saferoute.common.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST Controller.
 * Maneja login, registro, refresh token, logout y usuario actual.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "01. Autenticación", description = "Endpoints para autenticación y gestión de sesión")
public class AuthController {

    private final AuthAdapter authAdapter;

    /**
     * POST /api/v1/auth/login
     * Autentica al usuario y retorna los tokens JWT.
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica usuario con email y contraseña. Retorna access token y refresh token.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        log.info("Login attempt for email: {}", request.email());
        AuthResponse response = authAdapter.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/register
     * Registra un nuevo usuario en el sistema.
     */
    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario. Por defecto el rol es GUARDIAN.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        log.info("Registration attempt for email: {}", request.email());
        AuthResponse response = authAdapter.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/refresh
     * Renueva el access token usando el refresh token.
     */
    @Operation(summary = "Renovar token", description = "Usa el refresh token para obtener un nuevo access token.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh attempt");
        AuthResponse response = authAdapter.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     * Cierra la sesión del usuario actual.
     */
    @Operation(summary = "Cerrar sesión", description = "Invalida los tokens del usuario actual.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("Logout request");
        authAdapter.logout();
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/auth/me
     * Obtiene los datos del usuario autenticado.
     */
    @Operation(summary = "Usuario actual", description = "Retorna los datos del usuario autenticado actualmente.")
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        log.info("Get current user request");
        AuthResponse response = authAdapter.getCurrentUser();
        return ResponseEntity.ok(response);
    }
}

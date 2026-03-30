package com.saferoute.user.controller;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.user.adapter.UserAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * User REST Controller.
 * Maneja operaciones CRUD para usuarios del sistema.
 * Acceso: Solo ADMIN
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "02. Usuarios", description = "Gestión de usuarios del sistema (solo ADMIN)")
public class UserController {

    private final UserAdapter userAdapter;

    /**
     * GET /api/v1/users
     * Lista todos los usuarios del sistema.
     */
    @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios registrados. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        log.info("GET /api/v1/users - Fetching all users");
        List<UserResponse> users = userAdapter.getAll();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/{id}
     * Obtiene un usuario por su ID.
     */
    @Operation(summary = "Obtener usuario", description = "Retorna un usuario específico por ID. Solo ADMIN.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/users/{} - Fetching user by id", id);
        UserResponse user = userAdapter.getById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/v1/users
     * Crea un nuevo usuario en el sistema.
     */
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        log.info("POST /api/v1/users - Creating new user");
        UserResponse user = userAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * PUT /api/v1/users/{id}
     * Actualiza los datos de un usuario existente.
     */
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente. Solo ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {
        log.info("PUT /api/v1/users/{} - Updating user", id);
        UserResponse user = userAdapter.update(id, request);
        return ResponseEntity.ok(user);
    }

    /**
     * DELETE /api/v1/users/{id}
     * Elimina un usuario (soft delete).
     */
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (soft delete). Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);
        userAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}

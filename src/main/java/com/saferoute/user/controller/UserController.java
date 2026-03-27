package com.saferoute.user.controller;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.user.adapter.UserAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * User REST Controller.
 * Handles CRUD operations for users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserAdapter userAdapter;

    /**
     * GET /api/v1/users
     * Get all users.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        log.info("GET /api/v1/users - Fetching all users");
        List<UserResponse> users = userAdapter.getAll();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/{id}
     * Get user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/users/{} - Fetching user by id", id);
        UserResponse user = userAdapter.getById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/v1/users
     * Create a new user.
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        log.info("POST /api/v1/users - Creating new user");
        UserResponse user = userAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * PUT /api/v1/users/{id}
     * Update an existing user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {
        log.info("PUT /api/v1/users/{} - Updating user", id);
        UserResponse user = userAdapter.update(id, request);
        return ResponseEntity.ok(user);
    }

    /**
     * DELETE /api/v1/users/{id}
     * Delete a user (soft delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);
        userAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.saferoute.guardian.controller;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.guardian.adapter.GuardianAdapter;
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
 * Guardian REST Controller.
 * Handles CRUD operations for guardians.
 * Access: ADMIN (all), GUARDIAN (own), DRIVER (read only)
 */
@RestController
@RequestMapping("/api/v1/guardians")
@RequiredArgsConstructor
@Slf4j
public class GuardianController {

    private final GuardianAdapter guardianAdapter;

    /**
     * GET /api/v1/guardians
     * Get all guardians.
     * Access: ADMIN only
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GuardianResponse>> getAll() {
        log.info("GET /api/v1/guardians - Fetching all guardians");
        List<GuardianResponse> guardians = guardianAdapter.getAll();
        return ResponseEntity.ok(guardians);
    }

    /**
     * GET /api/v1/guardians/{id}
     * Get guardian by ID.
     * Access: ADMIN, GUARDIAN (own), DRIVER
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<GuardianResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/guardians/{} - Fetching guardian by id", id);
        GuardianResponse guardian = guardianAdapter.getById(id);
        return ResponseEntity.ok(guardian);
    }

    /**
     * POST /api/v1/guardians
     * Create a new guardian.
     * Access: ADMIN only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuardianResponse> create(@Valid @RequestBody GuardianRequest request) {
        log.info("POST /api/v1/guardians - Creating new guardian");
        GuardianResponse guardian = guardianAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardian);
    }

    /**
     * PUT /api/v1/guardians/{id}
     * Update an existing guardian.
     * Access: ADMIN, GUARDIAN (own)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<GuardianResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody GuardianRequest request) {
        log.info("PUT /api/v1/guardians/{} - Updating guardian", id);
        GuardianResponse guardian = guardianAdapter.update(id, request);
        return ResponseEntity.ok(guardian);
    }

    /**
     * DELETE /api/v1/guardians/{id}
     * Delete a guardian.
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/guardians/{} - Deleting guardian", id);
        guardianAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/guardians/{id}/fcm-token
     * Update FCM token for push notifications.
     * Access: ADMIN, GUARDIAN (own)
     */
    @PutMapping("/{id}/fcm-token")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<GuardianResponse> updateFcmToken(
            @PathVariable UUID id,
            @RequestBody FcmTokenRequest request) {
        log.info("PUT /api/v1/guardians/{}/fcm-token - Updating FCM token", id);
        GuardianResponse guardian = guardianAdapter.updateFcmToken(id, request.token());
        return ResponseEntity.ok(guardian);
    }

    /**
     * Request record for FCM token update.
     */
    public record FcmTokenRequest(String token) {}
}

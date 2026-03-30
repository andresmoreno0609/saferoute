package com.saferoute.notification.controller;

import com.saferoute.common.dto.notification.NotificationRequest;
import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.notification.adapter.NotificationAdapter;
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
 * Notification REST Controller.
 * Access: ADMIN (all), GUARDIAN (own), DRIVER (read only)
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationAdapter adapter;

    /**
     * POST /api/v1/notifications
     * Create notification (usually triggered by system).
     * Access: ADMIN, DRIVER (system)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        log.info("POST /api/v1/notifications");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/notifications
     * Get all notifications.
     * Access: ADMIN only
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getAll() {
        log.info("GET /api/v1/notifications");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/notifications/{id}
     * Get notification by ID.
     * Access: ADMIN, GUARDIAN (own)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/notifications/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * GET /api/v1/notifications/guardian/{guardianId}
     * Get notifications by guardian.
     * Access: ADMIN, GUARDIAN (own)
     */
    @GetMapping("/guardian/{guardianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<List<NotificationResponse>> getByGuardian(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/notifications/guardian/{}", guardianId);
        return ResponseEntity.ok(adapter.getByGuardianId(guardianId));
    }

    /**
     * GET /api/v1/notifications/guardian/{guardianId}/unread
     * Get unread notifications by guardian.
     * Access: ADMIN, GUARDIAN (own)
     */
    @GetMapping("/guardian/{guardianId}/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/notifications/guardian/{}/unread", guardianId);
        return ResponseEntity.ok(adapter.getUnread(guardianId));
    }

    /**
     * PUT /api/v1/notifications/{id}/read
     * Mark notification as read.
     * Access: ADMIN, GUARDIAN (own)
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        log.info("PUT /api/v1/notifications/{}/read", id);
        return ResponseEntity.ok(adapter.markAsRead(id));
    }

    /**
     * PUT /api/v1/notifications/guardian/{guardianId}/read-all
     * Mark all notifications as read for guardian.
     * Access: ADMIN, GUARDIAN (own)
     */
    @PutMapping("/guardian/{guardianId}/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable UUID guardianId) {
        log.info("PUT /api/v1/notifications/guardian/{}/read-all", guardianId);
        return ResponseEntity.ok(adapter.markAllAsRead(guardianId));
    }
}

package com.saferoute.notification.controller;

import com.saferoute.common.dto.notification.NotificationRequest;
import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.notification.adapter.NotificationAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationAdapter adapter;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        log.info("POST /api/v1/notifications");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        log.info("GET /api/v1/notifications");
        return ResponseEntity.ok(adapter.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/notifications/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    @GetMapping("/guardian/{guardianId}")
    public ResponseEntity<List<NotificationResponse>> getByGuardian(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/notifications/guardian/{}", guardianId);
        return ResponseEntity.ok(adapter.getByGuardianId(guardianId));
    }

    @GetMapping("/guardian/{guardianId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/notifications/guardian/{}/unread", guardianId);
        return ResponseEntity.ok(adapter.getUnread(guardianId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        log.info("PUT /api/v1/notifications/{}/read", id);
        return ResponseEntity.ok(adapter.markAsRead(id));
    }

    @PutMapping("/guardian/{guardianId}/read-all")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable UUID guardianId) {
        log.info("PUT /api/v1/notifications/guardian/{}/read-all", guardianId);
        return ResponseEntity.ok(adapter.markAllAsRead(guardianId));
    }
}

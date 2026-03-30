package com.saferoute.observation.controller;

import com.saferoute.common.dto.observation.ObservationRequest;
import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.observation.adapter.ObservationAdapter;
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
 * Observation REST Controller.
 * Access: ADMIN (all), DRIVER (create/read), GUARDIAN (read only)
 */
@RestController
@RequestMapping("/api/v1/observations")
@RequiredArgsConstructor
@Slf4j
public class ObservationController {

    private final ObservationAdapter adapter;

    /**
     * POST /api/v1/observations
     * Create observation.
     * Access: ADMIN, DRIVER
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<ObservationResponse> create(@Valid @RequestBody ObservationRequest request) {
        log.info("POST /api/v1/observations");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/observations
     * Get all observations.
     * Access: ADMIN only
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObservationResponse>> getAll() {
        log.info("GET /api/v1/observations");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/observations/{id}
     * Get observation by ID.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<ObservationResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/observations/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * GET /api/v1/observations/student/{studentId}
     * Get observations by student.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<ObservationResponse>> getByStudent(@PathVariable UUID studentId) {
        log.info("GET /api/v1/observations/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }
}

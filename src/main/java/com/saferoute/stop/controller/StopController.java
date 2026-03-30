package com.saferoute.stop.controller;

import com.saferoute.common.dto.stop.StopRequest;
import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.stop.adapter.StopAdapter;
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
 * Stop REST Controller.
 * Access: ADMIN (all), DRIVER (assigned routes), GUARDIAN (read only)
 */
@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
@Slf4j
public class StopController {

    private final StopAdapter adapter;

    /**
     * GET /api/v1/stops
     * Get all stops.
     * Access: ADMIN, DRIVER
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<StopResponse>> getAll() {
        log.info("GET /api/v1/stops");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/stops/{id}
     * Get stop by ID.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<StopResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/stops/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * POST /api/v1/stops
     * Create a new stop.
     * Access: ADMIN only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StopResponse> create(@Valid @RequestBody StopRequest request) {
        log.info("POST /api/v1/stops");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * PUT /api/v1/stops/{id}
     * Update an existing stop.
     * Access: ADMIN only
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StopResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StopRequest request) {
        log.info("PUT /api/v1/stops/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    /**
     * DELETE /api/v1/stops/{id}
     * Delete a stop.
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/stops/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/stops/{id}/picked-up
     * Mark student as picked up.
     * Access: ADMIN, DRIVER
     */
    @PutMapping("/{id}/picked-up")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StopResponse> markPickedUp(@PathVariable UUID id) {
        log.info("PUT /api/v1/stops/{}/picked-up", id);
        return ResponseEntity.ok(adapter.markPickedUp(id));
    }

    /**
     * PUT /api/v1/stops/{id}/dropped-off
     * Mark student as dropped off.
     * Access: ADMIN, DRIVER
     */
    @PutMapping("/{id}/dropped-off")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StopResponse> markDroppedOff(@PathVariable UUID id) {
        log.info("PUT /api/v1/stops/{}/dropped-off", id);
        return ResponseEntity.ok(adapter.markDroppedOff(id));
    }

    /**
     * GET /api/v1/stops/route/{routeId}
     * Get stops by route.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<StopResponse>> getByRouteId(@PathVariable UUID routeId) {
        log.info("GET /api/v1/stops/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }
}

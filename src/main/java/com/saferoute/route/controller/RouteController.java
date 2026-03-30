package com.saferoute.route.controller;

import com.saferoute.common.dto.route.RouteRequest;
import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.route.adapter.RouteAdapter;
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
 * Route REST Controller.
 * Access: ADMIN (all), DRIVER (assigned routes)
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteAdapter adapter;

    /**
     * GET /api/v1/routes
     * Get all routes.
     * Access: ADMIN, DRIVER
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<RouteResponse>> getAll() {
        log.info("GET /api/v1/routes");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/routes/{id}
     * Get route by ID.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<RouteResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/routes/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * POST /api/v1/routes
     * Create a new route.
     * Access: ADMIN only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest request) {
        log.info("POST /api/v1/routes");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * PUT /api/v1/routes/{id}
     * Update an existing route.
     * Access: ADMIN only
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RouteRequest request) {
        log.info("PUT /api/v1/routes/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    /**
     * DELETE /api/v1/routes/{id}
     * Delete a route.
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/routes/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/routes/{id}/start
     * Start a route.
     * Access: ADMIN, DRIVER
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteResponse> start(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/start", id);
        return ResponseEntity.ok(adapter.startRoute(id));
    }

    /**
     * POST /api/v1/routes/{id}/complete
     * Complete a route.
     * Access: ADMIN, DRIVER
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteResponse> complete(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/complete", id);
        return ResponseEntity.ok(adapter.completeRoute(id));
    }

    /**
     * POST /api/v1/routes/{id}/cancel
     * Cancel a route.
     * Access: ADMIN, DRIVER
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteResponse> cancel(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/cancel", id);
        return ResponseEntity.ok(adapter.cancelRoute(id));
    }
}

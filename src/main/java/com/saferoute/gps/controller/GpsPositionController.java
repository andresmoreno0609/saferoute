package com.saferoute.gps.controller;

import com.saferoute.common.dto.gps.GpsPositionRequest;
import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.gps.adapter.GpsPositionAdapter;
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
 * GPS Position REST Controller.
 * Access: ADMIN, DRIVER (own positions), GUARDIAN (read only)
 */
@RestController
@RequestMapping("/api/v1/gps")
@RequiredArgsConstructor
@Slf4j
public class GpsPositionController {

    private final GpsPositionAdapter adapter;

    /**
     * POST /api/v1/gps/position
     * Create GPS position.
     * Access: ADMIN, DRIVER
     */
    @PostMapping("/position")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<GpsPositionResponse> create(@Valid @RequestBody GpsPositionRequest request) {
        log.info("POST /api/v1/gps/position");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/gps/route/{routeId}
     * Get GPS positions by route.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<GpsPositionResponse>> getByRoute(@PathVariable UUID routeId) {
        log.info("GET /api/v1/gps/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }

    /**
     * GET /api/v1/gps/route/{routeId}/current
     * Get current GPS position for route.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/route/{routeId}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<GpsPositionResponse> getCurrentPosition(@PathVariable UUID routeId) {
        log.info("GET /api/v1/gps/route/{}/current", routeId);
        return ResponseEntity.ok(adapter.getCurrentPosition(routeId));
    }

    /**
     * GET /api/v1/gps/driver/{driverId}
     * Get GPS positions by driver.
     * Access: ADMIN, DRIVER
     */
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<GpsPositionResponse>> getByDriver(@PathVariable UUID driverId) {
        log.info("GET /api/v1/gps/driver/{}", driverId);
        return ResponseEntity.ok(adapter.getByDriverId(driverId));
    }
}

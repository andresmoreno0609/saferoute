package com.saferoute.gps.controller;

import com.saferoute.common.dto.gps.GpsPositionRequest;
import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.gps.adapter.GpsPositionAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gps")
@RequiredArgsConstructor
@Slf4j
public class GpsPositionController {

    private final GpsPositionAdapter adapter;

    @PostMapping("/position")
    public ResponseEntity<GpsPositionResponse> create(@Valid @RequestBody GpsPositionRequest request) {
        log.info("POST /api/v1/gps/position");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<GpsPositionResponse>> getByRoute(@PathVariable UUID routeId) {
        log.info("GET /api/v1/gps/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }

    @GetMapping("/route/{routeId}/current")
    public ResponseEntity<GpsPositionResponse> getCurrentPosition(@PathVariable UUID routeId) {
        log.info("GET /api/v1/gps/route/{}/current", routeId);
        return ResponseEntity.ok(adapter.getCurrentPosition(routeId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<GpsPositionResponse>> getByDriver(@PathVariable UUID driverId) {
        log.info("GET /api/v1/gps/driver/{}", driverId);
        return ResponseEntity.ok(adapter.getByDriverId(driverId));
    }
}

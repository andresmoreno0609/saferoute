package com.saferoute.route.controller;

import com.saferoute.common.dto.route.RouteRequest;
import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.route.adapter.RouteAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Route REST Controller.
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteAdapter adapter;

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAll() {
        log.info("GET /api/v1/routes");
        return ResponseEntity.ok(adapter.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/routes/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    @PostMapping
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest request) {
        log.info("POST /api/v1/routes");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RouteRequest request) {
        log.info("PUT /api/v1/routes/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/routes/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<RouteResponse> start(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/start", id);
        return ResponseEntity.ok(adapter.startRoute(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RouteResponse> complete(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/complete", id);
        return ResponseEntity.ok(adapter.completeRoute(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<RouteResponse> cancel(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/cancel", id);
        return ResponseEntity.ok(adapter.cancelRoute(id));
    }
}

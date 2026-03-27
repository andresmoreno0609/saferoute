package com.saferoute.stop.controller;

import com.saferoute.common.dto.stop.StopRequest;
import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.stop.adapter.StopAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Stop REST Controller.
 */
@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
@Slf4j
public class StopController {

    private final StopAdapter adapter;

    @GetMapping
    public ResponseEntity<List<StopResponse>> getAll() {
        log.info("GET /api/v1/stops");
        return ResponseEntity.ok(adapter.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StopResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/stops/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    @PostMapping
    public ResponseEntity<StopResponse> create(@Valid @RequestBody StopRequest request) {
        log.info("POST /api/v1/stops");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StopResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StopRequest request) {
        log.info("PUT /api/v1/stops/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/stops/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/picked-up")
    public ResponseEntity<StopResponse> markPickedUp(@PathVariable UUID id) {
        log.info("PUT /api/v1/stops/{}/picked-up", id);
        return ResponseEntity.ok(adapter.markPickedUp(id));
    }

    @PutMapping("/{id}/dropped-off")
    public ResponseEntity<StopResponse> markDroppedOff(@PathVariable UUID id) {
        log.info("PUT /api/v1/stops/{}/dropped-off", id);
        return ResponseEntity.ok(adapter.markDroppedOff(id));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<StopResponse>> getByRouteId(@PathVariable UUID routeId) {
        log.info("GET /api/v1/stops/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }
}

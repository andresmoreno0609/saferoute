package com.saferoute.observation.controller;

import com.saferoute.common.dto.observation.ObservationRequest;
import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.observation.adapter.ObservationAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/observations")
@RequiredArgsConstructor
@Slf4j
public class ObservationController {

    private final ObservationAdapter adapter;

    @PostMapping
    public ResponseEntity<ObservationResponse> create(@Valid @RequestBody ObservationRequest request) {
        log.info("POST /api/v1/observations");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ObservationResponse>> getAll() {
        log.info("GET /api/v1/observations");
        return ResponseEntity.ok(adapter.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservationResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/observations/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ObservationResponse>> getByStudent(@PathVariable UUID studentId) {
        log.info("GET /api/v1/observations/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }
}

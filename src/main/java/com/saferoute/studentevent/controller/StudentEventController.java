package com.saferoute.studentevent.controller;

import com.saferoute.common.dto.studentevent.StudentEventRequest;
import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.studentevent.adapter.StudentEventAdapter;
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
 * Student Event REST Controller.
 * Access: ADMIN, DRIVER (create/read), GUARDIAN (read only)
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class StudentEventController {

    private final StudentEventAdapter adapter;

    /**
     * POST /api/v1/events
     * Create student event (BOARD, ARRIVAL, DROP).
     * Access: ADMIN, DRIVER
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StudentEventResponse> create(@Valid @RequestBody StudentEventRequest request) {
        log.info("POST /api/v1/events");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/events/student/{studentId}
     * Get events by student.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<StudentEventResponse>> getByStudent(@PathVariable UUID studentId) {
        log.info("GET /api/v1/events/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }

    /**
     * GET /api/v1/events/route/{routeId}
     * Get events by route.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<StudentEventResponse>> getByRoute(@PathVariable UUID routeId) {
        log.info("GET /api/v1/events/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }

    /**
     * GET /api/v1/events/student/{studentId}/route/{routeId}/last
     * Get last event for student on route.
     * Access: ADMIN, DRIVER, GUARDIAN
     */
    @GetMapping("/student/{studentId}/route/{routeId}/last")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<StudentEventResponse> getLastEvent(
            @PathVariable UUID studentId,
            @PathVariable UUID routeId) {
        log.info("GET /api/v1/events/student/{}/route/{}/last", studentId, routeId);
        return ResponseEntity.ok(adapter.getLastEvent(studentId, routeId));
    }
}

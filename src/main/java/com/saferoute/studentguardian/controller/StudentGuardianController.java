package com.saferoute.studentguardian.controller;

import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.studentguardian.adapter.StudentGuardianAdapter;
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
 * Student-Guardian Relationship REST Controller.
 * Access: ADMIN (all), GUARDIAN (own), DRIVER (read only)
 */
@RestController
@RequestMapping("/api/v1/student-guardians")
@RequiredArgsConstructor
@Slf4j
public class StudentGuardianController {

    private final StudentGuardianAdapter adapter;

    /**
     * GET /api/v1/student-guardians
     * Get all relationships.
     * Access: ADMIN only
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentGuardianResponse>> getAll() {
        log.info("GET /api/v1/student-guardians");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/student-guardians/{id}
     * Get relationship by ID.
     * Access: ADMIN, GUARDIAN (own), DRIVER
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<StudentGuardianResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/student-guardians/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * POST /api/v1/student-guardians
     * Create a new relationship.
     * Access: ADMIN only
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentGuardianResponse> create(@Valid @RequestBody StudentGuardianRequest request) {
        log.info("POST /api/v1/student-guardians");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * PUT /api/v1/student-guardians/{id}
     * Update a relationship.
     * Access: ADMIN only
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentGuardianResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentGuardianRequest request) {
        log.info("PUT /api/v1/student-guardians/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    /**
     * DELETE /api/v1/student-guardians/{id}
     * Delete a relationship.
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/student-guardians/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/student-guardians/student/{studentId}
     * Get all guardians for a student.
     * Access: ADMIN, GUARDIAN (own), DRIVER
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<List<StudentGuardianResponse>> getByStudentId(@PathVariable UUID studentId) {
        log.info("GET /api/v1/student-guardians/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }

    /**
     * GET /api/v1/student-guardians/guardian/{guardianId}
     * Get all students for a guardian.
     * Access: ADMIN, GUARDIAN (own), DRIVER
     */
    @GetMapping("/guardian/{guardianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<List<StudentGuardianResponse>> getByGuardianId(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/student-guardians/guardian/{}", guardianId);
        return ResponseEntity.ok(adapter.getByGuardianId(guardianId));
    }
}

package com.saferoute.student.controller;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.student.adapter.StudentAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Student REST Controller.
 * Handles CRUD operations for students.
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentAdapter studentAdapter;

    /**
     * GET /api/v1/students
     * Get all students.
     */
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        log.info("GET /api/v1/students - Fetching all students");
        List<StudentResponse> students = studentAdapter.getAll();
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/v1/students/{id}
     * Get student by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/students/{} - Fetching student by id", id);
        StudentResponse student = studentAdapter.getById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * POST /api/v1/students
     * Create a new student.
     */
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        log.info("POST /api/v1/students - Creating new student");
        StudentResponse student = studentAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    /**
     * PUT /api/v1/students/{id}
     * Update an existing student.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequest request) {
        log.info("PUT /api/v1/students/{} - Updating student", id);
        StudentResponse student = studentAdapter.update(id, request);
        return ResponseEntity.ok(student);
    }

    /**
     * DELETE /api/v1/students/{id}
     * Delete a student.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/students/{} - Deleting student", id);
        studentAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}

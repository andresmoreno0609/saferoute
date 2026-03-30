package com.saferoute.student.controller;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.student.adapter.StudentAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Student REST Controller.
 * Maneja operaciones CRUD para estudiantes.
 * Acceso: ADMIN (todo), DRIVER (lectura), GUARDIAN (sus estudiantes)
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "04. Estudiantes", description = "Gestión de estudiantes del sistema")
public class StudentController {

    private final StudentAdapter studentAdapter;

    /**
     * GET /api/v1/students
     * Lista todos los estudiantes registrados.
     */
    @Operation(summary = "Listar estudiantes", description = "Retorna todos los estudiantes. ADMIN y DRIVER.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<StudentResponse>> getAll() {
        log.info("GET /api/v1/students - Fetching all students");
        List<StudentResponse> students = studentAdapter.getAll();
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/v1/students/{id}
     * Obtiene un estudiante por su ID.
     */
    @Operation(summary = "Obtener estudiante", description = "Retorna un estudiante específico por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<StudentResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/students/{} - Fetching student by id", id);
        StudentResponse student = studentAdapter.getById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * POST /api/v1/students
     * Crea un nuevo estudiante en el sistema.
     */
    @Operation(summary = "Crear estudiante", description = "Registra un nuevo estudiante. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        log.info("POST /api/v1/students - Creating new student");
        StudentResponse student = studentAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    /**
     * PUT /api/v1/students/{id}
     * Actualiza los datos de un estudiante existente.
     */
    @Operation(summary = "Actualizar estudiante", description = "Actualiza los datos de un estudiante existente. Solo ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequest request) {
        log.info("PUT /api/v1/students/{} - Updating student", id);
        StudentResponse student = studentAdapter.update(id, request);
        return ResponseEntity.ok(student);
    }

    /**
     * DELETE /api/v1/students/{id}
     * Elimina un estudiante del sistema.
     */
    @Operation(summary = "Eliminar estudiante", description = "Elimina un estudiante del sistema. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/students/{} - Deleting student", id);
        studentAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}

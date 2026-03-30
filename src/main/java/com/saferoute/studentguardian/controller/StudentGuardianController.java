package com.saferoute.studentguardian.controller;

import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.studentguardian.adapter.StudentGuardianAdapter;
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
 * Student-Guardian Relationship REST Controller.
 * Maneja las relaciones entre estudiantes y acudientes.
 * Acceso: ADMIN (todo), GUARDIAN (propio), DRIVER (lectura)
 */
@RestController
@RequestMapping("/api/v1/student-guardians")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "06. Relaciones Estudiante-Acudiente", description = "Gestión de vínculos entre estudiantes y acudientes")
public class StudentGuardianController {

    private final StudentGuardianAdapter adapter;

    /**
     * GET /api/v1/student-guardians
     * Lista todas las relaciones estudiante-acudiente.
     */
    @Operation(summary = "Listar relaciones", description = "Retorna todas las relaciones. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentGuardianResponse>> getAll() {
        log.info("GET /api/v1/student-guardians");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/student-guardians/{id}
     * Obtiene una relación por su ID.
     */
    @Operation(summary = "Obtener relación", description = "Retorna una relación específica por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<StudentGuardianResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/student-guardians/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * POST /api/v1/student-guardians
     * Crea una nueva relación estudiante-acudiente.
     */
    @Operation(summary = "Crear relación", description = "Asocia un estudiante con un acudiente. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentGuardianResponse> create(@Valid @RequestBody StudentGuardianRequest request) {
        log.info("POST /api/v1/student-guardians");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * PUT /api/v1/student-guardians/{id}
     * Actualiza una relación existente.
     */
    @Operation(summary = "Actualizar relación", description = "Actualiza una relación existente. Solo ADMIN.")
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
     * Elimina una relación.
     */
    @Operation(summary = "Eliminar relación", description = "Elimina la relación entre estudiante y acudiente. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/student-guardians/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/student-guardians/student/{studentId}
     * Lista todos los acudientes de un estudiante.
     */
    @Operation(summary = "Acudientes por estudiante", description = "Retorna todos los acudientes asociados a un estudiante.")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<List<StudentGuardianResponse>> getByStudentId(@PathVariable UUID studentId) {
        log.info("GET /api/v1/student-guardians/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }

    /**
     * GET /api/v1/student-guardians/guardian/{guardianId}
     * Lista todos los estudiantes de un acudiente.
     */
    @Operation(summary = "Estudiantes por acudiente", description = "Retorna todos los estudiantes asociados a un acudiente.")
    @GetMapping("/guardian/{guardianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<List<StudentGuardianResponse>> getByGuardianId(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/student-guardians/guardian/{}", guardianId);
        return ResponseEntity.ok(adapter.getByGuardianId(guardianId));
    }
}

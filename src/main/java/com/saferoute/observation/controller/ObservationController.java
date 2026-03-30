package com.saferoute.observation.controller;

import com.saferoute.common.dto.observation.ObservationRequest;
import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.observation.adapter.ObservationAdapter;
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
 * Observation REST Controller.
 * Maneja las observaciones/novedades reportadas sobre estudiantes.
 * Acceso: ADMIN (todo), DRIVER (crear/leer), GUARDIAN (lectura)
 */
@RestController
@RequestMapping("/api/v1/observations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "11. Observaciones", description = "Gestión de observaciones y reportes de novedades")
public class ObservationController {

    private final ObservationAdapter adapter;

    /**
     * POST /api/v1/observations
     * Crea una nueva observación/novedad sobre un estudiante.
     */
    @Operation(summary = "Crear observación", description = "Registra una observación o novedad sobre un estudiante (comportamiento, incidentes, etc.).")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<ObservationResponse> create(@Valid @RequestBody ObservationRequest request) {
        log.info("POST /api/v1/observations");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/observations
     * Lista todas las observaciones registradas.
     */
    @Operation(summary = "Listar observaciones", description = "Retorna todas las observaciones. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObservationResponse>> getAll() {
        log.info("GET /api/v1/observations");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/observations/{id}
     * Obtiene una observación por su ID.
     */
    @Operation(summary = "Obtener observación", description = "Retorna una observación específica por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<ObservationResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/observations/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * GET /api/v1/observations/student/{studentId}
     * Obtiene todas las observaciones de un estudiante.
     */
    @Operation(summary = "Observaciones por estudiante", description = "Retorna todas las observaciones asociadas a un estudiante.")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<ObservationResponse>> getByStudent(@PathVariable UUID studentId) {
        log.info("GET /api/v1/observations/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }
}

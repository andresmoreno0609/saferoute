package com.saferoute.studentevent.controller;

import com.saferoute.common.dto.studentevent.StudentEventRequest;
import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.studentevent.adapter.StudentEventAdapter;
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
 * Student Event REST Controller.
 * Maneja los eventos de estudiantes: BOARD (subida), ARRIVAL (llegada), DROP (dejada).
 * Acceso: ADMIN, DRIVER (crear/leer), GUARDIAN (lectura)
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "10. Eventos de Estudiantes", description = "Registro de eventos: subida, llegada a escuela, dejada en casa")
public class StudentEventController {

    private final StudentEventAdapter adapter;

    /**
     * POST /api/v1/events
     * Registra un evento de estudiante (BOARD, ARRIVAL, DROP).
     */
    @Operation(summary = "Registrar evento", description = "Registra un evento: BOARD (subida), ARRIVAL (llegada), DROP (dejada).")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StudentEventResponse> create(@Valid @RequestBody StudentEventRequest request) {
        log.info("POST /api/v1/events");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/events/student/{studentId}
     * Obtiene todos los eventos de un estudiante.
     */
    @Operation(summary = "Eventos por estudiante", description = "Retorna el historial de eventos de un estudiante.")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<StudentEventResponse>> getByStudent(@PathVariable UUID studentId) {
        log.info("GET /api/v1/events/student/{}", studentId);
        return ResponseEntity.ok(adapter.getByStudentId(studentId));
    }

    /**
     * GET /api/v1/events/route/{routeId}
     * Obtiene todos los eventos de una ruta.
     */
    @Operation(summary = "Eventos por ruta", description = "Retorna todos los eventos registrados en una ruta.")
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<StudentEventResponse>> getByRoute(@PathVariable UUID routeId) {
        log.info("GET /api/v1/events/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }

    /**
     * GET /api/v1/events/student/{studentId}/route/{routeId}/last
     * Obtiene el último evento de un estudiante en una ruta.
     */
    @Operation(summary = "Último evento", description = "Retorna el último evento de un estudiante en una ruta específica.")
    @GetMapping("/student/{studentId}/route/{routeId}/last")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<StudentEventResponse> getLastEvent(
            @PathVariable UUID studentId,
            @PathVariable UUID routeId) {
        log.info("GET /api/v1/events/student/{}/route/{}/last", studentId, routeId);
        return ResponseEntity.ok(adapter.getLastEvent(studentId, routeId));
    }
}

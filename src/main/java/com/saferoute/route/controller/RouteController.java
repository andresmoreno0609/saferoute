package com.saferoute.route.controller;

import com.saferoute.common.dto.route.RouteRequest;
import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.route.adapter.RouteAdapter;
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
 * Route REST Controller.
 * Maneja las operaciones CRUD y gestión de estado de rutas escolares.
 * Acceso: ADMIN (todo), DRIVER (asignadas), GUARDIAN (lectura)
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "07. Rutas", description = "Gestión de rutas escolares y control de estado")
public class RouteController {

    private final RouteAdapter adapter;

    /**
     * GET /api/v1/routes
     * Lista todas las rutas registradas.
     */
    @Operation(summary = "Listar rutas", description = "Retorna todas las rutas. ADMIN y DRIVER.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<RouteResponse>> getAll() {
        log.info("GET /api/v1/routes");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/routes/{id}
     * Obtiene una ruta por su ID.
     */
    @Operation(summary = "Obtener ruta", description = "Retorna una ruta específica por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<RouteResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/routes/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * POST /api/v1/routes
     * Crea una nueva ruta escolar.
     */
    @Operation(summary = "Crear ruta", description = "Registra una nueva ruta escolar. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest request) {
        log.info("POST /api/v1/routes");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * PUT /api/v1/routes/{id}
     * Actualiza los datos de una ruta existente.
     */
    @Operation(summary = "Actualizar ruta", description = "Actualiza los datos de una ruta existente. Solo ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RouteResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RouteRequest request) {
        log.info("PUT /api/v1/routes/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    /**
     * DELETE /api/v1/routes/{id}
     * Elimina una ruta del sistema.
     */
    @Operation(summary = "Eliminar ruta", description = "Elimina una ruta del sistema. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/routes/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/routes/{id}/start
     * Inicia una ruta (cambia estado a IN_PROGRESS).
     */
    @Operation(summary = "Iniciar ruta", description = "Marca la ruta como iniciada. El conductor comienza el recorrido.")
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteResponse> start(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/start", id);
        return ResponseEntity.ok(adapter.startRoute(id));
    }

    /**
     * POST /api/v1/routes/{id}/complete
     * Completa una ruta (cambia estado a COMPLETED).
     */
    @Operation(summary = "Completar ruta", description = "Marca la ruta como completada. Finaliza el recorrido.")
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteResponse> complete(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/complete", id);
        return ResponseEntity.ok(adapter.completeRoute(id));
    }

    /**
     * POST /api/v1/routes/{id}/cancel
     * Cancela una ruta (cambia estado a CANCELLED).
     */
    @Operation(summary = "Cancelar ruta", description = "Cancela una ruta en curso.")
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<RouteResponse> cancel(@PathVariable UUID id) {
        log.info("POST /api/v1/routes/{}/cancel", id);
        return ResponseEntity.ok(adapter.cancelRoute(id));
    }
}

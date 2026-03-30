package com.saferoute.stop.controller;

import com.saferoute.common.dto.stop.StopRequest;
import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.stop.adapter.StopAdapter;
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
 * Stop REST Controller.
 * Maneja las paradas de las rutas escolares.
 * Acceso: ADMIN (todo), DRIVER (asignadas), GUARDIAN (lectura)
 */
@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "08. Paradas", description = "Gestión de paradas en las rutas escolares")
public class StopController {

    private final StopAdapter adapter;

    /**
     * GET /api/v1/stops
     * Lista todas las paradas registradas.
     */
    @Operation(summary = "Listar paradas", description = "Retorna todas las paradas. ADMIN y DRIVER.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<StopResponse>> getAll() {
        log.info("GET /api/v1/stops");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/stops/{id}
     * Obtiene una parada por su ID.
     */
    @Operation(summary = "Obtener parada", description = "Retorna una parada específica por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<StopResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/stops/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * POST /api/v1/stops
     * Crea una nueva parada en una ruta.
     */
    @Operation(summary = "Crear parada", description = "Registra una nueva parada en una ruta. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StopResponse> create(@Valid @RequestBody StopRequest request) {
        log.info("POST /api/v1/stops");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * PUT /api/v1/stops/{id}
     * Actualiza los datos de una parada existente.
     */
    @Operation(summary = "Actualizar parada", description = "Actualiza los datos de una parada existente. Solo ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StopResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody StopRequest request) {
        log.info("PUT /api/v1/stops/{}", id);
        return ResponseEntity.ok(adapter.update(id, request));
    }

    /**
     * DELETE /api/v1/stops/{id}
     * Elimina una parada del sistema.
     */
    @Operation(summary = "Eliminar parada", description = "Elimina una parada del sistema. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/stops/{}", id);
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/stops/{id}/picked-up
     * Marca al estudiante como recogido.
     */
    @Operation(summary = "Marcar recogido", description = "Marca al estudiante como recogido en la parada.")
    @PutMapping("/{id}/picked-up")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StopResponse> markPickedUp(@PathVariable UUID id) {
        log.info("PUT /api/v1/stops/{}/picked-up", id);
        return ResponseEntity.ok(adapter.markPickedUp(id));
    }

    /**
     * PUT /api/v1/stops/{id}/dropped-off
     * Marca al estudiante como dejado en casa.
     */
    @Operation(summary = "Marcar dejado", description = "Marca al estudiante como dejado en su domicilio.")
    @PutMapping("/{id}/dropped-off")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StopResponse> markDroppedOff(@PathVariable UUID id) {
        log.info("PUT /api/v1/stops/{}/dropped-off", id);
        return ResponseEntity.ok(adapter.markDroppedOff(id));
    }

    /**
     * GET /api/v1/stops/route/{routeId}
     * Lista todas las paradas de una ruta específica.
     */
    @Operation(summary = "Paradas por ruta", description = "Retorna todas las paradas de una ruta específica.")
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<StopResponse>> getByRouteId(@PathVariable UUID routeId) {
        log.info("GET /api/v1/stops/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }
}

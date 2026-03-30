package com.saferoute.gps.controller;

import com.saferoute.common.dto.gps.GpsPositionRequest;
import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.gps.adapter.GpsPositionAdapter;
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
 * GPS Position REST Controller.
 * Maneja el tracking de posición GPS de los autobuses.
 * Acceso: ADMIN, DRIVER (propias), GUARDIAN (lectura)
 */
@RestController
@RequestMapping("/api/v1/gps")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "09. GPS Tracking", description = "Seguimiento de posición GPS del autobús")
public class GpsPositionController {

    private final GpsPositionAdapter adapter;

    /**
     * POST /api/v1/gps/position
     * Envía la posición GPS actual del autobús.
     */
    @Operation(summary = "Enviar posición", description = "Registra la posición GPS actual del autobús. DRIVER o ADMIN.")
    @PostMapping("/position")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<GpsPositionResponse> create(@Valid @RequestBody GpsPositionRequest request) {
        log.info("POST /api/v1/gps/position");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/gps/route/{routeId}
     * Obtiene el historial de posiciones de una ruta.
     */
    @Operation(summary = "Historial por ruta", description = "Retorna el historial de posiciones GPS de una ruta.")
    @GetMapping("/route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<List<GpsPositionResponse>> getByRoute(@PathVariable UUID routeId) {
        log.info("GET /api/v1/gps/route/{}", routeId);
        return ResponseEntity.ok(adapter.getByRouteId(routeId));
    }

    /**
     * GET /api/v1/gps/route/{routeId}/current
     * Obtiene la posición actual del autobús en una ruta.
     */
    @Operation(summary = "Posición actual", description = "Retorna la última posición conocida del autobús en la ruta.")
    @GetMapping("/route/{routeId}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<GpsPositionResponse> getCurrentPosition(@PathVariable UUID routeId) {
        log.info("GET /api/v1/gps/route/{}/current", routeId);
        return ResponseEntity.ok(adapter.getCurrentPosition(routeId));
    }

    /**
     * GET /api/v1/gps/driver/{driverId}
     * Obtiene el historial de posiciones de un conductor.
     */
    @Operation(summary = "Historial por conductor", description = "Retorna el historial de posiciones GPS de un conductor.")
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<GpsPositionResponse>> getByDriver(@PathVariable UUID driverId) {
        log.info("GET /api/v1/gps/driver/{}", driverId);
        return ResponseEntity.ok(adapter.getByDriverId(driverId));
    }
}

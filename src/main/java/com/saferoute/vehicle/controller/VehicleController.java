package com.saferoute.vehicle.controller;

import com.saferoute.common.dto.vehicle.VehicleRequest;
import com.saferoute.common.dto.vehicle.VehicleResponse;
import com.saferoute.vehicle.adapter.VehicleAdapter;
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
 * Vehicle REST Controller.
 * Maneja operaciones CRUD para vehículos.
 * Acceso: ADMIN (todo), DRIVER (lectura propia)
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "14. Vehículos", description = "Gestión de vehículos del sistema")
public class VehicleController {

    private final VehicleAdapter vehicleAdapter;

    /**
     * GET /api/v1/vehicles
     * Lista todos los vehículos registrados.
     */
    @Operation(summary = "Listar vehículos", description = "Retorna todos los vehículos. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleResponse>> getAll() {
        log.info("GET /api/v1/vehicles - Fetching all vehicles");
        List<VehicleResponse> vehicles = vehicleAdapter.getAll();
        return ResponseEntity.ok(vehicles);
    }

    /**
     * GET /api/v1/vehicles/{id}
     * Obtiene un vehículo por su ID.
     */
    @Operation(summary = "Obtener vehículo", description = "Retorna un vehículo específico por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/vehicles/{} - Fetching vehicle by id", id);
        VehicleResponse vehicle = vehicleAdapter.getById(id);
        return ResponseEntity.ok(vehicle);
    }

    /**
     * POST /api/v1/vehicles
     * Crea un nuevo vehículo en el sistema.
     */
    @Operation(summary = "Crear vehículo", description = "Registra un nuevo vehículo. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        log.info("POST /api/v1/vehicles - Creating new vehicle");
        VehicleResponse vehicle = vehicleAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    /**
     * PUT /api/v1/vehicles/{id}
     * Actualiza los datos de un vehículo existente.
     */
    @Operation(summary = "Actualizar vehículo", description = "Actualiza los datos de un vehículo existente. Solo ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request) {
        log.info("PUT /api/v1/vehicles/{} - Updating vehicle", id);
        VehicleResponse vehicle = vehicleAdapter.update(id, request);
        return ResponseEntity.ok(vehicle);
    }

    /**
     * DELETE /api/v1/vehicles/{id}
     * Elimina un vehículo del sistema.
     */
    @Operation(summary = "Eliminar vehículo", description = "Elimina un vehículo del sistema. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/vehicles/{} - Deleting vehicle", id);
        vehicleAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}
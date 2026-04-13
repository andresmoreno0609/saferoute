package com.saferoute.driver.controller;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.service.DriverAvailabilityService;
import com.saferoute.driver.adapter.DriverAdapter;
import com.saferoute.driver.usecase.BecomeDriverFromUserUseCase;
import com.saferoute.driver.usecase.BecomeDriverFromUserRequest;
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
 * Driver REST Controller.
 * Maneja operaciones CRUD para conductores.
 * Acceso: ADMIN (todo), DRIVER (lectura propia), GUARDIAN (lectura)
 */
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "03. Conductores", description = "Gestión de conductores del sistema")
public class DriverController {

    private final DriverAdapter driverAdapter;
    private final DriverAvailabilityService driverAvailabilityService;
    private final BecomeDriverFromUserUseCase becomeDriverFromUserUseCase;

    /**
     * POST /api/v1/drivers/from-user/{userId}
     * Permite a un usuario existente convertirse en conductor.
     * Agrega el rol DRIVER al usuario y crea el perfil de conductor.
     */
    @Operation(summary = "Volverse conductor", description = "Un usuario se convierte en conductor. Agrega rol DRIVER y crea perfil.")
    @PostMapping("/from-user/{userId}")
    public ResponseEntity<DriverResponse> becomeDriver(
            @PathVariable UUID userId,
            @Valid @RequestBody BecomeDriverFromUserRequest request) {
        log.info("POST /api/v1/drivers/from-user/{} - User becoming driver", userId);
        // Override the userId in request with path variable
        BecomeDriverFromUserRequest fullRequest = new BecomeDriverFromUserRequest(
                userId,
                request.name(),
                request.phone(),
                request.documentNumber(),
                request.birthDate(),
                request.address(),
                request.licenseNumber(),
                request.licenseCategory(),
                request.licenseExpirationDate(),
                request.emergencyContact(),
                request.emergencyPhone(),
                request.yearsExperience(),
                request.photoUrl(),
                request.bankName(),
                request.bankAccount(),
                request.vehicleId()
        );
        DriverResponse driver = becomeDriverFromUserUseCase.execute(fullRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    /**
     * GET /api/v1/drivers
     * Lista todos los conductores registrados.
     */
    @Operation(summary = "Listar conductores", description = "Retorna todos los conductores. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DriverResponse>> getAll() {
        log.info("GET /api/v1/drivers - Fetching all drivers");
        List<DriverResponse> drivers = driverAdapter.getAll();
        return ResponseEntity.ok(drivers);
    }

    /**
     * GET /api/v1/drivers/{id}
     * Obtiene un conductor por su ID.
     */
    @Operation(summary = "Obtener conductor", description = "Retorna un conductor específico por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'GUARDIAN')")
    public ResponseEntity<DriverResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/drivers/{} - Fetching driver by id", id);
        DriverResponse driver = driverAdapter.getById(id);
        return ResponseEntity.ok(driver);
    }

    /**
     * POST /api/v1/drivers
     * Crea un nuevo conductor en el sistema.
     */
    @Operation(summary = "Crear conductor", description = "Registra un nuevo conductor. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        log.info("POST /api/v1/drivers - Creating new driver");
        DriverResponse driver = driverAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    /**
     * PUT /api/v1/drivers/{id}
     * Actualiza los datos de un conductor existente.
     */
    @Operation(summary = "Actualizar conductor", description = "Actualiza los datos de un conductor existente. Solo ADMIN.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DriverRequest request) {
        log.info("PUT /api/v1/drivers/{} - Updating driver", id);
        DriverResponse driver = driverAdapter.update(id, request);
        return ResponseEntity.ok(driver);
    }

    /**
     * DELETE /api/v1/drivers/{id}
     * Elimina un conductor del sistema.
     */
    @Operation(summary = "Eliminar conductor", description = "Elimina un conductor del sistema. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/drivers/{} - Deleting driver", id);
        driverAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/drivers/{id}/availability
     * Verifica si el conductor puede trabajar (documentos vigentes).
     */
    @Operation(summary = "Verificar disponibilidad", description = "Retorna si el conductor puede trabajar basado en sus documentos.")
    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<DriverAvailabilityService.AvailabilityResult> checkAvailability(@PathVariable UUID id) {
        log.info("GET /api/v1/drivers/{}/availability - Checking driver availability", id);
        DriverAvailabilityService.AvailabilityResult result = driverAvailabilityService.checkAvailability(id);
        return ResponseEntity.ok(result);
    }
}

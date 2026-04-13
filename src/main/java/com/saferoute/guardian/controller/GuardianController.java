package com.saferoute.guardian.controller;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.guardian.adapter.GuardianAdapter;
import com.saferoute.guardian.usecase.BecomeGuardianFromUserUseCase;
import com.saferoute.guardian.usecase.BecomeGuardianFromUserRequest;
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
 * Guardian REST Controller.
 * Maneja operaciones CRUD para acudientes.
 * Acceso: ADMIN (todo), GUARDIAN (propio), DRIVER (lectura)
 */
@RestController
@RequestMapping("/api/v1/guardians")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "05. Acudientes", description = "Gestión de acudientes y tokens FCM")
public class GuardianController {

    private final GuardianAdapter guardianAdapter;
    private final BecomeGuardianFromUserUseCase becomeGuardianFromUserUseCase;

    /**
     * POST /api/v1/guardians/from-user/{userId}
     * Permite a un usuario existente convertirse en acudiente.
     * Agrega el rol GUARDIAN al usuario y crea el perfil de acudiente.
     */
    @Operation(summary = "Volverse acudiente", description = "Un usuario se convierte en acudiente. Agrega rol GUARDIAN y crea perfil.")
    @PostMapping("/from-user/{userId}")
    public ResponseEntity<GuardianResponse> becomeGuardian(
            @PathVariable UUID userId,
            @Valid @RequestBody BecomeGuardianFromUserRequest request) {
        log.info("POST /api/v1/guardians/from-user/{} - User becoming guardian", userId);
        // Override the userId in request with path variable
        BecomeGuardianFromUserRequest fullRequest = new BecomeGuardianFromUserRequest(
                userId,
                request.name(),
                request.phone(),
                request.email(),
                request.documentNumber(),
                request.address(),
                request.emergencyContact(),
                request.emergencyPhone(),
                request.occupation(),
                request.workPhone()
        );
        GuardianResponse guardian = becomeGuardianFromUserUseCase.execute(fullRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardian);
    }

    /**
     * GET /api/v1/guardians
     * Lista todos los acudientes registrados.
     */
    @Operation(summary = "Listar acudientes", description = "Retorna todos los acudientes. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GuardianResponse>> getAll() {
        log.info("GET /api/v1/guardians - Fetching all guardians");
        List<GuardianResponse> guardians = guardianAdapter.getAll();
        return ResponseEntity.ok(guardians);
    }

    /**
     * GET /api/v1/guardians/{id}
     * Obtiene un acudiente por su ID.
     */
    @Operation(summary = "Obtener acudiente", description = "Retorna un acudiente específico por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<GuardianResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/guardians/{} - Fetching guardian by id", id);
        GuardianResponse guardian = guardianAdapter.getById(id);
        return ResponseEntity.ok(guardian);
    }

    /**
     * POST /api/v1/guardians
     * Crea un nuevo acudiente en el sistema.
     */
    @Operation(summary = "Crear acudiente", description = "Registra un nuevo acudiente. Solo ADMIN.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GuardianResponse> create(@Valid @RequestBody GuardianRequest request) {
        log.info("POST /api/v1/guardians - Creating new guardian");
        GuardianResponse guardian = guardianAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardian);
    }

    /**
     * PUT /api/v1/guardians/{id}
     * Actualiza los datos de un acudiente existente.
     */
    @Operation(summary = "Actualizar acudiente", description = "Actualiza los datos de un acudiente existente.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<GuardianResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody GuardianRequest request) {
        log.info("PUT /api/v1/guardians/{} - Updating guardian", id);
        GuardianResponse guardian = guardianAdapter.update(id, request);
        return ResponseEntity.ok(guardian);
    }

    /**
     * DELETE /api/v1/guardians/{id}
     * Elimina un acudiente del sistema.
     */
    @Operation(summary = "Eliminar acudiente", description = "Elimina un acudiente del sistema. Solo ADMIN.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/guardians/{} - Deleting guardian", id);
        guardianAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/guardians/{id}/fcm-token
     * Actualiza el token FCM para notificaciones push.
     */
    @Operation(summary = "Actualizar token FCM", description = "Actualiza el token de Firebase Cloud Messaging para recibir notificaciones push.")
    @PutMapping("/{id}/fcm-token")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<GuardianResponse> updateFcmToken(
            @PathVariable UUID id,
            @RequestBody FcmTokenRequest request) {
        log.info("PUT /api/v1/guardians/{}/fcm-token - Updating FCM token", id);
        GuardianResponse guardian = guardianAdapter.updateFcmToken(id, request.token());
        return ResponseEntity.ok(guardian);
    }

    /**
     * Request record for FCM token update.
     */
    public record FcmTokenRequest(String token) {}
}

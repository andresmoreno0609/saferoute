package com.saferoute.guardian.controller;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.guardian.adapter.GuardianAdapter;
import com.saferoute.guardian.dto.GuardianStudentData;
import com.saferoute.guardian.service.GuardianStudentService;
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
    private final GuardianStudentService guardianStudentService;
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

/**
     * GET /api/v1/guardians/user/{userId}
     * Obtiene un acudiente por el ID del usuario.
     */
    @Operation(summary = "Obtener acudiente por userId", description = "Retorna el perfil de un acudiente vinculado al usuario.")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN', 'DRIVER')")
    public ResponseEntity<GuardianResponse> getByUserId(@PathVariable UUID userId) {
        log.info("GET /api/v1/guardians/user/{} - Fetching guardian by userId", userId);
        GuardianResponse guardian = guardianAdapter.getByUserId(userId);
        return ResponseEntity.ok(guardian);
    }

    // ========== Gestión de hijos por el propio guardian ==========

    /**
     * POST /api/v1/guardians/{guardianId}/students
     * Crea un estudiante y lo vincula al guardian automáticamente.
     */
    @Operation(summary = "Crear hijo", description = "Crea un estudiante y lo vincula al guardian.")
    @PostMapping("/{guardianId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<StudentGuardianResponse> createStudent(
            @PathVariable UUID guardianId,
            @Valid @RequestBody GuardianStudentData request) {
        log.info("POST /api/v1/guardians/{}/students - Creating student for guardian", guardianId);
        StudentGuardianResponse response = guardianStudentService.createStudentForGuardian(guardianId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/guardians/{guardianId}/students/{studentId}
     * Actualiza un estudiante verificando que pertenezca al guardian.
     */
    @Operation(summary = "Actualizar hijo", description = "Actualiza los datos de un estudiante del guardian.")
    @PutMapping("/{guardianId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<StudentGuardianResponse> updateStudent(
            @PathVariable UUID guardianId,
            @PathVariable UUID studentId,
            @Valid @RequestBody GuardianStudentData request) {
        log.info("PUT /api/v1/guardians/{}/students/{} - Updating student", guardianId, studentId);
        StudentGuardianResponse response = guardianStudentService.updateStudentForGuardian(guardianId, studentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/guardians/{guardianId}/students/{studentId}
     * Elimina un estudiante verificando que no esté en rutas activas.
     */
    @Operation(summary = "Eliminar hijo", description = "Elimina un estudiante. Verifica que no esté asignado a rutas.")
    @DeleteMapping("/{guardianId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable UUID guardianId,
            @PathVariable UUID studentId) {
        log.info("DELETE /api/v1/guardians/{}/students/{} - Deleting student", guardianId, studentId);
        guardianStudentService.deleteStudentForGuardian(guardianId, studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/guardians/{guardianId}/students
     * Lista todos los hijos de un guardian.
     */
    @Operation(summary = "Listar hijos", description = "Retorna la lista de estudiantes vinculados al guardian.")
    @GetMapping("/{guardianId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<List<StudentGuardianResponse>> getStudents(
            @PathVariable UUID guardianId) {
        log.info("GET /api/v1/guardians/{}/students - Listing students", guardianId);
        List<StudentGuardianResponse> students = guardianStudentService.getStudentsByGuardian(guardianId);
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/v1/guardians/{guardianId}/students/{studentId}
     * Obtiene un estudiante específico verificando que pertenezca al guardian.
     */
    @Operation(summary = "Obtener hijo", description = "Retorna los datos de un estudiante específico del guardian.")
    @GetMapping("/{guardianId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<StudentGuardianResponse> getStudent(
            @PathVariable UUID guardianId,
            @PathVariable UUID studentId) {
        log.info("GET /api/v1/guardians/{}/students/{} - Getting student", guardianId, studentId);
        StudentGuardianResponse student = guardianStudentService.getStudentForGuardian(guardianId, studentId);
        return ResponseEntity.ok(student);
    }

}

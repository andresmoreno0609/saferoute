package com.saferoute.notification.controller;

import com.saferoute.common.dto.notification.NotificationRequest;
import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.notification.adapter.NotificationAdapter;
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
 * Notification REST Controller.
 * Maneja las notificaciones push para acudientes.
 * Acceso: ADMIN (todo), GUARDIAN (propias), DRIVER (crear)
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "12. Notificaciones", description = "Gestión de notificaciones push para padres y acudientes")
public class NotificationController {

    private final NotificationAdapter adapter;

    /**
     * POST /api/v1/notifications
     * Crea una nueva notificación (usualmente disparada por el sistema).
     */
    @Operation(summary = "Crear notificación", description = "Envía una notificación push a un acudiente (eventos: BOARD, ARRIVAL, DROP, OBSERVATION).")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        log.info("POST /api/v1/notifications");
        return ResponseEntity.status(HttpStatus.CREATED).body(adapter.create(request));
    }

    /**
     * GET /api/v1/notifications
     * Lista todas las notificaciones del sistema.
     */
    @Operation(summary = "Listar notificaciones", description = "Retorna todas las notificaciones. Solo ADMIN.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getAll() {
        log.info("GET /api/v1/notifications");
        return ResponseEntity.ok(adapter.getAll());
    }

    /**
     * GET /api/v1/notifications/{id}
     * Obtiene una notificación por su ID.
     */
    @Operation(summary = "Obtener notificación", description = "Retorna una notificación específica por ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/notifications/{}", id);
        return ResponseEntity.ok(adapter.getById(id));
    }

    /**
     * GET /api/v1/notifications/guardian/{guardianId}
     * Obtiene todas las notificaciones de un acudiente.
     */
    @Operation(summary = "Notificaciones por acudiente", description = "Retorna todas las notificaciones de un acudiente.")
    @GetMapping("/guardian/{guardianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<List<NotificationResponse>> getByGuardian(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/notifications/guardian/{}", guardianId);
        return ResponseEntity.ok(adapter.getByGuardianId(guardianId));
    }

    /**
     * GET /api/v1/notifications/guardian/{guardianId}/unread
     * Obtiene las notificaciones no leídas de un acudiente.
     */
    @Operation(summary = "Notificaciones no leídas", description = "Retorna las notificaciones no leídas de un acudiente.")
    @GetMapping("/guardian/{guardianId}/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<List<NotificationResponse>> getUnread(@PathVariable UUID guardianId) {
        log.info("GET /api/v1/notifications/guardian/{}/unread", guardianId);
        return ResponseEntity.ok(adapter.getUnread(guardianId));
    }

    /**
     * PUT /api/v1/notifications/{id}/read
     * Marca una notificación como leída.
     */
    @Operation(summary = "Marcar como leída", description = "Marca una notificación como leída.")
    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        log.info("PUT /api/v1/notifications/{}/read", id);
        return ResponseEntity.ok(adapter.markAsRead(id));
    }

    /**
     * PUT /api/v1/notifications/guardian/{guardianId}/read-all
     * Marca todas las notificaciones como leídas.
     */
    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones de un acudiente como leídas.")
    @PutMapping("/guardian/{guardianId}/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIAN')")
    public ResponseEntity<Integer> markAllAsRead(@PathVariable UUID guardianId) {
        log.info("PUT /api/v1/notifications/guardian/{}/read-all", guardianId);
        return ResponseEntity.ok(adapter.markAllAsRead(guardianId));
    }
}

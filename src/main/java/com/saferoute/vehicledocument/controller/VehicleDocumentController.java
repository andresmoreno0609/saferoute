package com.saferoute.vehicledocument.controller;

import com.saferoute.common.dto.vehicledocument.RejectDocumentRequest;
import com.saferoute.common.dto.vehicledocument.VehicleDocumentRequest;
import com.saferoute.common.dto.vehicledocument.VehicleDocumentResponse;
import com.saferoute.common.dto.vehicledocument.VerifyDocumentRequest;
import com.saferoute.vehicledocument.adapter.VehicleDocumentAdapter;
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
 * Vehicle Document REST Controller.
 * Maneja operaciones CRUD para documentos de vehículos.
 */
@RestController
@RequestMapping("/api/v1/vehicles/{vehicleId}/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "14. Vehículos - Documentos", description = "Gestión de documentos de vehículos")
public class VehicleDocumentController {

    private final VehicleDocumentAdapter vehicleDocumentAdapter;

    /**
     * GET /api/v1/vehicles/{vehicleId}/documents
     * Lista todos los documentos de un vehículo.
     */
    @Operation(summary = "Listar documentos del vehículo", description = "Retorna todos los documentos del vehículo.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<VehicleDocumentResponse>> getAll(@PathVariable UUID vehicleId) {
        log.info("GET /api/v1/vehicles/{}/documents - Fetching all documents", vehicleId);
        List<VehicleDocumentResponse> documents = vehicleDocumentAdapter.getByVehicle(vehicleId);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/v1/vehicles/{vehicleId}/documents/active
     * Lista solo los documentos activos del vehículo.
     */
    @Operation(summary = "Documentos activos", description = "Retorna solo los documentos activos del vehículo.")
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<VehicleDocumentResponse>> getActive(@PathVariable UUID vehicleId) {
        log.info("GET /api/v1/vehicles/{}/documents/active - Fetching active documents", vehicleId);
        List<VehicleDocumentResponse> documents = vehicleDocumentAdapter.getActiveByVehicle(vehicleId);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/v1/vehicles/{vehicleId}/documents/{documentId}
     * Obtiene un documento específico.
     */
    @Operation(summary = "Obtener documento", description = "Retorna un documento específico por ID.")
    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<VehicleDocumentResponse> getById(
            @PathVariable UUID vehicleId,
            @PathVariable UUID documentId) {
        log.info("GET /api/v1/vehicles/{}/documents/{} - Fetching document", vehicleId, documentId);
        VehicleDocumentResponse document = vehicleDocumentAdapter.getById(documentId);
        return ResponseEntity.ok(document);
    }

    /**
     * POST /api/v1/vehicles/{vehicleId}/documents
     * Agrega un nuevo documento al vehículo.
     */
    @Operation(summary = "Agregar documento", description = "Agrega un nuevo documento al vehículo. Inactiva el anterior del mismo tipo.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDocumentResponse> create(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody VehicleDocumentRequest request) {
        log.info("POST /api/v1/vehicles/{}/documents - Creating new document", vehicleId);
        VehicleDocumentResponse document = vehicleDocumentAdapter.create(vehicleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    /**
     * PUT /api/v1/vehicles/{vehicleId}/documents/{documentId}
     * Actualiza un documento existente.
     */
    @Operation(summary = "Actualizar documento", description = "Actualiza un documento existente del vehículo.")
    @PutMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDocumentResponse> update(
            @PathVariable UUID vehicleId,
            @PathVariable UUID documentId,
            @Valid @RequestBody VehicleDocumentRequest request) {
        log.info("PUT /api/v1/vehicles/{}/documents/{} - Updating document", vehicleId, documentId);
        VehicleDocumentResponse document = vehicleDocumentAdapter.update(documentId, request);
        return ResponseEntity.ok(document);
    }

    /**
     * DELETE /api/v1/vehicles/{vehicleId}/documents/{documentId}
     * Elimina un documento (soft delete).
     */
    @Operation(summary = "Eliminar documento", description = "Elimina un documento del vehículo (soft delete).")
    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID vehicleId,
            @PathVariable UUID documentId) {
        log.info("DELETE /api/v1/vehicles/{}/documents/{} - Deleting document", vehicleId, documentId);
        vehicleDocumentAdapter.delete(documentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/vehicles/{vehicleId}/documents/{documentId}/verify
     * Verificar documento (Admin revisa y aprueba)
     */
    @Operation(summary = "Verificar documento", description = "El ADMIN verifica el documento después de revisar en páginas de terceros.")
    @PostMapping("/{documentId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDocumentResponse> verify(
            @PathVariable UUID vehicleId,
            @PathVariable UUID documentId,
            @Valid @RequestBody VerifyDocumentRequest request) {
        log.info("POST /api/v1/vehicles/{}/documents/{}/verify - Verifying document", vehicleId, documentId);
        UUID verifiedBy = UUID.fromString(request.verifiedBy());
        VehicleDocumentResponse document = vehicleDocumentAdapter.verify(documentId, verifiedBy);
        return ResponseEntity.ok(document);
    }

    /**
     * POST /api/v1/vehicles/{vehicleId}/documents/{documentId}/reject
     * Rechazar documento (Admin)
     */
    @Operation(summary = "Rechazar documento", description = "El ADMIN rechaza el documento indicando el motivo.")
    @PostMapping("/{documentId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDocumentResponse> reject(
            @PathVariable UUID vehicleId,
            @PathVariable UUID documentId,
            @Valid @RequestBody RejectDocumentRequest request) {
        log.info("POST /api/v1/vehicles/{}/documents/{}/reject - Rejecting document", vehicleId, documentId);
        UUID verifiedBy = UUID.fromString(request.verifiedBy());
        VehicleDocumentResponse document = vehicleDocumentAdapter.reject(documentId, verifiedBy, request.reason());
        return ResponseEntity.ok(document);
    }
}
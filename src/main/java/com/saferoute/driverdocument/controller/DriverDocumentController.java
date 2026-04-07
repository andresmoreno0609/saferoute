package com.saferoute.driverdocument.controller;

import com.saferoute.common.dto.driverdocument.DriverDocumentRequest;
import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.dto.vehicledocument.RejectDocumentRequest;
import com.saferoute.common.dto.vehicledocument.VerifyDocumentRequest;
import com.saferoute.driverdocument.adapter.DriverDocumentAdapter;
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
 * Driver Document REST Controller.
 * Maneja operaciones CRUD para documentos del conductor.
 */
@RestController
@RequestMapping("/api/v1/drivers/{driverId}/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "03. Conductores - Documentos", description = "Gestión de documentos del conductor")
public class DriverDocumentController {

    private final DriverDocumentAdapter driverDocumentAdapter;

    /**
     * GET /api/v1/drivers/{driverId}/documents
     * Lista todos los documentos de un conductor.
     */
    @Operation(summary = "Listar documentos del conductor", description = "Retorna todos los documentos del conductor.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DriverDocumentResponse>> getAll(@PathVariable UUID driverId) {
        log.info("GET /api/v1/drivers/{}/documents - Fetching all documents", driverId);
        List<DriverDocumentResponse> documents = driverDocumentAdapter.getByDriver(driverId);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/v1/drivers/{driverId}/documents/active
     * Lista solo los documentos activos del conductor.
     */
    @Operation(summary = "Documentos activos", description = "Retorna solo los documentos activos del conductor.")
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<List<DriverDocumentResponse>> getActive(@PathVariable UUID driverId) {
        log.info("GET /api/v1/drivers/{}/documents/active - Fetching active documents", driverId);
        List<DriverDocumentResponse> documents = driverDocumentAdapter.getActiveByDriver(driverId);
        return ResponseEntity.ok(documents);
    }

    /**
     * GET /api/v1/drivers/{driverId}/documents/{documentId}
     * Obtiene un documento específico.
     */
    @Operation(summary = "Obtener documento", description = "Retorna un documento específico por ID.")
    @GetMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<DriverDocumentResponse> getById(
            @PathVariable UUID driverId,
            @PathVariable UUID documentId) {
        log.info("GET /api/v1/drivers/{}/documents/{} - Fetching document", driverId, documentId);
        DriverDocumentResponse document = driverDocumentAdapter.getById(documentId);
        return ResponseEntity.ok(document);
    }

    /**
     * POST /api/v1/drivers/{driverId}/documents
     * Agrega un nuevo documento al conductor.
     */
    @Operation(summary = "Agregar documento", description = "Agrega un nuevo documento al conductor. Inactiva el anterior del mismo tipo.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDocumentResponse> create(
            @PathVariable UUID driverId,
            @Valid @RequestBody DriverDocumentRequest request) {
        log.info("POST /api/v1/drivers/{}/documents - Creating new document", driverId);
        DriverDocumentResponse document = driverDocumentAdapter.create(driverId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    /**
     * PUT /api/v1/drivers/{driverId}/documents/{documentId}
     * Actualiza un documento existente.
     */
    @Operation(summary = "Actualizar documento", description = "Actualiza un documento existente del conductor.")
    @PutMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDocumentResponse> update(
            @PathVariable UUID driverId,
            @PathVariable UUID documentId,
            @Valid @RequestBody DriverDocumentRequest request) {
        log.info("PUT /api/v1/drivers/{}/documents/{} - Updating document", driverId, documentId);
        DriverDocumentResponse document = driverDocumentAdapter.update(documentId, request);
        return ResponseEntity.ok(document);
    }

    /**
     * DELETE /api/v1/drivers/{driverId}/documents/{documentId}
     * Elimina un documento (soft delete).
     */
    @Operation(summary = "Eliminar documento", description = "Elimina un documento del conductor (soft delete).")
    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID driverId,
            @PathVariable UUID documentId) {
        log.info("DELETE /api/v1/drivers/{}/documents/{} - Deleting document", driverId, documentId);
        driverDocumentAdapter.delete(documentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/drivers/{driverId}/documents/{documentId}/verify
     * Verificar documento del conductor (Admin revisa y aprueba)
     */
    @Operation(summary = "Verificar documento", description = "El ADMIN verifica el documento después de revisar.")
    @PostMapping("/{documentId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDocumentResponse> verify(
            @PathVariable UUID driverId,
            @PathVariable UUID documentId,
            @Valid @RequestBody VerifyDocumentRequest request) {
        log.info("POST /api/v1/drivers/{}/documents/{}/verify - Verifying document", driverId, documentId);
        UUID verifiedBy = UUID.fromString(request.verifiedBy());
        DriverDocumentResponse document = driverDocumentAdapter.verify(documentId, verifiedBy);
        return ResponseEntity.ok(document);
    }

    /**
     * POST /api/v1/drivers/{driverId}/documents/{documentId}/reject
     * Rechazar documento del conductor (Admin)
     */
    @Operation(summary = "Rechazar documento", description = "El ADMIN rechaza el documento indicando el motivo.")
    @PostMapping("/{documentId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverDocumentResponse> reject(
            @PathVariable UUID driverId,
            @PathVariable UUID documentId,
            @Valid @RequestBody RejectDocumentRequest request) {
        log.info("POST /api/v1/drivers/{}/documents/{}/reject - Rejecting document", driverId, documentId);
        UUID verifiedBy = UUID.fromString(request.verifiedBy());
        DriverDocumentResponse document = driverDocumentAdapter.reject(documentId, verifiedBy, request.reason());
        return ResponseEntity.ok(document);
    }
}
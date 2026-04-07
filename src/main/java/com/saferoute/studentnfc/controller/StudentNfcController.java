package com.saferoute.studentnfc.controller;

import com.saferoute.studentnfc.adapter.StudentNfcAdapter;
import com.saferoute.studentnfc.dto.AssignNfcRequest;
import com.saferoute.studentnfc.dto.NfcScanRequest;
import com.saferoute.studentnfc.dto.StudentNfcResponse;
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
 * StudentNFC REST Controller.
 * Maneja operaciones de tarjetas NFC para estudiantes.
 * Acceso: ADMIN (todo), DRIVER (escanear)
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "05. NFC", description = "Gestión de tarjetas NFC para estudiantes")
public class StudentNfcController {

    private final StudentNfcAdapter studentNfcAdapter;

    /**
     * POST /api/v1/students/{studentId}/nfc
     * Asignar NFC a estudiante
     */
    @Operation(summary = "Asignar NFC", description = "Asigna una tarjeta NFC a un estudiante. Solo ADMIN.")
    @PostMapping("/students/{studentId}/nfc")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentNfcResponse> assignNfc(
            @PathVariable UUID studentId,
            @Valid @RequestBody AssignNfcRequest request) {
        log.info("POST /api/v1/students/{}/nfc - Assigning NFC", studentId);
        StudentNfcResponse response = studentNfcAdapter.assignNfc(studentId, request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/students/{studentId}/nfc
     * Obtener NFC activo del estudiante
     */
    @Operation(summary = "Obtener NFC activo", description = "Retorna el NFC activo de un estudiante.")
    @GetMapping("/students/{studentId}/nfc")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StudentNfcResponse> getActiveNfc(@PathVariable UUID studentId) {
        log.info("GET /api/v1/students/{}/nfc - Getting active NFC", studentId);
        StudentNfcResponse response = studentNfcAdapter.getActiveNfc(studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/students/{studentId}/nfc
     * Desactivar NFC del estudiante
     */
    @Operation(summary = "Desactivar NFC", description = "Desactiva el NFC activo de un estudiante. Solo ADMIN.")
    @DeleteMapping("/students/{studentId}/nfc")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateNfc(@PathVariable UUID studentId) {
        log.info("DELETE /api/v1/students/{}/nfc - Deactivating NFC", studentId);
        studentNfcAdapter.deactivateNfc(studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/students/{studentId}/nfc/history
     * Ver historial de NFCs del estudiante
     */
    @Operation(summary = "Historial NFC", description = "Retorna el historial de tarjetas NFC del estudiante.")
    @GetMapping("/students/{studentId}/nfc/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentNfcResponse>> getNfcHistory(@PathVariable UUID studentId) {
        log.info("GET /api/v1/students/{}/nfc/history - Getting NFC history", studentId);
        List<StudentNfcResponse> response = studentNfcAdapter.getNfcHistory(studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/nfc/scan
     * Escanear NFC (detectar estudiante)
     */
    @Operation(summary = "Escanear NFC", description = "Escanea una tarjeta NFC y retorna el estudiante asociado.")
    @PostMapping("/nfc/scan")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    public ResponseEntity<StudentNfcResponse> scanNfc(@Valid @RequestBody NfcScanRequest request) {
        log.info("POST /api/v1/nfc/scan - Scanning NFC: {}", request.nfcUid());
        StudentNfcResponse response = studentNfcAdapter.scanNfc(request);
        return ResponseEntity.ok(response);
    }
}
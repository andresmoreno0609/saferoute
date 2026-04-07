package com.saferoute.studentnfc.usecase;

import com.saferoute.common.service.StudentNfcService;
import com.saferoute.common.usecase.UseCaseAdvance;
import com.saferoute.studentnfc.dto.NfcScanRequest;
import com.saferoute.studentnfc.dto.StudentNfcResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Use case for scanning an NFC tag to find the associated student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScanNfcUseCase extends UseCaseAdvance<NfcScanRequest, StudentNfcResponse> {

    private final StudentNfcService studentNfcService;

    @Override
    protected void preConditions(NfcScanRequest request) {
        if (request == null || request.nfcUid() == null || request.nfcUid().isBlank()) {
            throw new IllegalArgumentException("El UID del NFC es obligatorio");
        }
    }

    @Override
    protected StudentNfcResponse core(NfcScanRequest request) {
        log.info("Scanning NFC: {}", request.nfcUid());
        
        var nfc = studentNfcService.findActiveByNfcUid(request.nfcUid());
        
        if (nfc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "NFC no encontrado o inactivo: " + request.nfcUid());
        }
        
        return toResponse(nfc);
    }

    private StudentNfcResponse toResponse(com.saferoute.common.entity.StudentNfcEntity entity) {
        return StudentNfcResponse.builder()
                .id(entity.getId())
                .studentId(entity.getStudent().getId())
                .studentName(entity.getStudent().getName())
                .nfcUid(entity.getNfcUid())
                .isActive(entity.getIsActive())
                .assignedAt(entity.getAssignedAt())
                .deactivatedAt(entity.getDeactivatedAt())
                .assignedBy(entity.getAssignedBy())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
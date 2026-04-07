package com.saferoute.studentnfc.usecase;

import com.saferoute.common.service.StudentNfcService;
import com.saferoute.common.usecase.UseCaseAdvance;
import com.saferoute.studentnfc.dto.StudentNfcResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Use case for getting the active NFC for a student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetActiveNfcUseCase extends UseCaseAdvance<UUID, StudentNfcResponse> {

    private final StudentNfcService studentNfcService;

    @Override
    protected void preConditions(UUID studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
    }

    @Override
    protected StudentNfcResponse core(UUID studentId) {
        log.debug("Fetching active NFC for student {}", studentId);
        
        var nfc = studentNfcService.getActiveByStudentId(studentId);
        
        if (nfc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay NFC activo para este estudiante");
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
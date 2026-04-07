package com.saferoute.studentnfc.usecase;

import com.saferoute.common.service.StudentNfcService;
import com.saferoute.common.usecase.UseCaseAdvance;
import com.saferoute.studentnfc.dto.AssignNfcRequest;
import com.saferoute.studentnfc.dto.StudentNfcResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for assigning an NFC tag to a student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AssignNfcUseCase extends UseCaseAdvance<AssignNfcUseCaseRequest, StudentNfcResponse> {

    private final StudentNfcService studentNfcService;

    @Override
    protected void preConditions(AssignNfcUseCaseRequest request) {
        if (request.studentId() == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
        if (request.request() == null || request.request().nfcUid() == null || request.request().nfcUid().isBlank()) {
            throw new IllegalArgumentException("El UID del NFC es obligatorio");
        }
    }

    @Override
    protected StudentNfcResponse core(AssignNfcUseCaseRequest request) {
        log.info("Assigning NFC {} to student {}", request.request().nfcUid(), request.studentId());
        
        var nfc = studentNfcService.assignNfc(
                request.studentId(),
                request.request().nfcUid(),
                request.assignedBy(),
                request.request().notes()
        );

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
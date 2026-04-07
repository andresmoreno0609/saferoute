package com.saferoute.studentnfc.usecase;

import com.saferoute.common.service.StudentNfcService;
import com.saferoute.common.usecase.UseCaseAdvance;
import com.saferoute.studentnfc.dto.StudentNfcResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Use case for getting NFC history for a student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetNfcHistoryUseCase extends UseCaseAdvance<UUID, List<StudentNfcResponse>> {

    private final StudentNfcService studentNfcService;

    @Override
    protected void preConditions(UUID studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
    }

    @Override
    protected List<StudentNfcResponse> core(UUID studentId) {
        log.debug("Fetching NFC history for student {}", studentId);
        
        var nfcs = studentNfcService.findByStudentId(studentId);
        
        return nfcs.stream()
                .map(this::toResponse)
                .toList();
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
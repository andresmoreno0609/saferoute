package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.repository.StudentGuardianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Obtiene un estudiante específico verificando que pertenezca al guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetGuardianStudentUseCase {

    private final StudentGuardianRepository studentGuardianRepository;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    public StudentGuardianResponse executeWithIds(UUID guardianId, UUID studentId) {
        // 1. Verificar que el guardian pertenece al usuario autenticado
        verifyOwnershipUseCase.execute(guardianId);

        // 2. Buscar la relación entre estudiante y guardian
        StudentGuardianEntity relation = studentGuardianRepository
                .findByStudentIdAndGuardianId(studentId, guardianId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "El estudiante no pertenece a este guardian"));

        // 3. Obtener los datos del estudiante desde la relación
        var student = relation.getStudent();

        log.info("Retrieved student {} for guardian {}", studentId, guardianId);

        return new StudentGuardianResponse(
                relation.getId(),
                student.getId(),
                student.getName(),
                guardianId,
                relation.getGuardian() != null ? relation.getGuardian().getName() : null,
                relation.getRelationship(),
                relation.getIsEmergencyContact(),
                relation.getNotifyEvents(),
                relation.getCreatedAt()
        );
    }

    /**
     * Request record para el use case.
     */
    public record GuardianStudentRequest(UUID guardianId, UUID studentId) {}
}
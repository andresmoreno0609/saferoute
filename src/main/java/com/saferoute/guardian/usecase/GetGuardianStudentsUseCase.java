package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Lista todos los hijos de un guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetGuardianStudentsUseCase extends UseCaseAdvance<UUID, List<StudentGuardianResponse>> {

    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentRepository studentRepository;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    @Override
    protected List<StudentGuardianResponse> core(UUID guardianId) {
        // 1. Verificar ownership del guardian
        verifyOwnershipUseCase.execute(guardianId);

        // 2. Obtener relaciones
        List<StudentGuardianEntity> relations = studentGuardianRepository.findByGuardianId(guardianId);

        // 3. Mapear a response
        return relations.stream()
                .map(rel -> {
                    var student = studentRepository.findById(rel.getStudentId())
                            .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado: " + rel.getStudentId()));
                    return new StudentGuardianResponse(
                            rel.getId(),
                            student.getId(),
                            student.getName(),
                            rel.getGuardianId(),
                            null,
                            rel.getRelationship(),
                            rel.getIsEmergencyContact(),
                            rel.getNotifyEvents(),
                            rel.getCreatedAt()
                    );
                })
                .toList();
    }
}
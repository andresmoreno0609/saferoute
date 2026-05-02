package com.saferoute.guardian.usecase;

import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.repository.StudentRepository;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Elimina un estudiante verificando que no esté en rutas y que pertenezca al guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteGuardianStudentUseCase extends UseCaseAdvance<UUID, Void> {

    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentRepository studentRepository;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    public void executeWithIds(UUID guardianId, UUID studentId) {
        verifyOwnershipUseCase.execute(guardianId);

        StudentGuardianEntity relation = studentGuardianRepository
                .findByStudentIdAndGuardianId(studentId, guardianId)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este guardian"));

        studentGuardianRepository.delete(relation);

        List<StudentGuardianEntity> remainingRelations = studentGuardianRepository.findByStudentId(studentId);
        if (remainingRelations.isEmpty()) {
            studentRepository.deleteById(studentId);
            log.info("Deleted student {} (no more guardians)", studentId);
        } else {
            log.info("Removed student {} from guardian {}, still has {} guardians",
                    studentId, guardianId, remainingRelations.size());
        }
    }

    @Override
    protected Void core(UUID id) {
        throw new UnsupportedOperationException("Use executeWithIds instead");
    }
}
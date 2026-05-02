package com.saferoute.guardian.usecase;

import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.repository.StudentRepository;
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
public class DeleteGuardianStudentUseCase extends UseCaseAdvance<DeleteGuardianStudentRequest, Void> {

    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentRepository studentRepository;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    @Override
    protected Void core(DeleteGuardianStudentRequest request) {
        // 1. Verificar ownership del guardian
        verifyOwnershipUseCase.execute(request.guardianId());

        // 2. Verificar que el estudiante pertenece a este guardian
        StudentGuardianEntity relation = studentGuardianRepository
                .findByStudentIdAndGuardianId(request.studentId(), request.guardianId())
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este guardian"));

        // 3. Verificar que no esté en rutas activas (implementar según modelo)
        // Por ahora solo eliminos la relación

        // 4. Eliminar relación
        studentGuardianRepository.delete(relation);

        // 5. Eliminar estudiante solo si no tiene más guardianes
        List<StudentGuardianEntity> remainingRelations = studentGuardianRepository.findByStudentId(request.studentId());
        if (remainingRelations.isEmpty()) {
            studentRepository.deleteById(request.studentId());
            log.info("Deleted student {} (no more guardians)", request.studentId());
        } else {
            log.info("Removed student {} from guardian {}, still has {} guardians",
                    request.studentId(), request.guardianId(), remainingRelations.size());
        }

        return null;
    }
}

record DeleteGuardianStudentRequest(UUID guardianId, UUID studentId) {}
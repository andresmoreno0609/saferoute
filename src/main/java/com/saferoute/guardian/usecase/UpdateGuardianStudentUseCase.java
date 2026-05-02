package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Actualiza un estudiante verificando que pertenezca al guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateGuardianStudentUseCase extends UseCaseAdvance<UpdateGuardianStudentRequest, StudentGuardianResponse> {

    private final StudentService studentService;
    private final StudentGuardianRepository studentGuardianRepository;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    @Override
    protected StudentGuardianResponse core(UpdateGuardianStudentRequest request) {
        // 1. Verificar ownership del guardian
        verifyOwnershipUseCase.execute(request.guardianId());

        // 2. Verificar que el estudiante pertenece a este guardian
        StudentGuardianEntity relation = studentGuardianRepository
                .findByStudentIdAndGuardianId(request.studentId(), request.guardianId())
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este guardian"));

        // 3. Actualizar estudiante
        StudentRequest studentRequest = new StudentRequest(
                request.studentData().name(),
                request.studentData().address(),
                request.studentData().homeLatitude(),
                request.studentData().homeLongitude(),
                request.studentData().schoolName(),
                request.studentData().schoolLatitude(),
                request.studentData().schoolLongitude(),
                request.studentData().grade(),
                request.studentData().birthDate(),
                request.studentData().emergencyContact(),
                request.studentData().emergencyPhone(),
                request.studentData().medicalInfo(),
                request.studentData().photoUrl(),
                request.studentData().studentCode()
        );
        StudentResponse student = studentService.update(request.studentId(), studentRequest);

        // 4. Actualizar relación
        relation.setRelationship(request.studentData().relationship());
        relation.setIsEmergencyContact(booleanOrDefault(request.studentData().isEmergencyContact(), false));
        relation.setNotifyEvents(booleanOrDefault(request.studentData().notifyEvents(), true));
        studentGuardianRepository.save(relation);

        log.info("Updated student {} for guardian {}", request.studentId(), request.guardianId());
        return toResponse(relation, student);
    }

    private boolean booleanOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    private StudentGuardianResponse toResponse(StudentGuardianEntity relation, StudentResponse student) {
        return new StudentGuardianResponse(
                relation.getId(),
                student.id(),
                student.name(),
                relation.getGuardianId(),
                null,
                relation.getRelationship(),
                relation.getIsEmergencyContact(),
                relation.getNotifyEvents(),
                relation.getCreatedAt()
        );
    }
}

record UpdateGuardianStudentRequest(UUID guardianId, UUID studentId, GuardianStudentData studentData) {}
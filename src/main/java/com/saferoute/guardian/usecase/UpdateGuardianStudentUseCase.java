package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.service.StudentService;
import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import com.saferoute.guardian.dto.GuardianStudentData;
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
public class UpdateGuardianStudentUseCase extends UseCaseAdvance<GuardianStudentData, StudentGuardianResponse> {

    private final StudentService studentService;
    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentGuardianService studentGuardianService;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    public StudentGuardianResponse executeWithIds(UUID guardianId, UUID studentId, GuardianStudentData data) {
        verifyOwnershipUseCase.execute(guardianId);

        var relation = studentGuardianRepository.findByStudentIdAndGuardianId(studentId, guardianId)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este guardian"));

        StudentRequest studentRequest = toStudentRequest(data);
        StudentResponse student = studentService.update(studentId, studentRequest);

        StudentGuardianRequest relationRequest = new StudentGuardianRequest(
                studentId,
                guardianId,
                data.relationship(),
                booleanOrDefault(data.isEmergencyContact(), false),
                booleanOrDefault(data.notifyEvents(), true)
        );
        
        studentGuardianService.update(relation.getId(), relationRequest);

        log.info("Updated student {} for guardian {}", studentId, guardianId);
        return new StudentGuardianResponse(
                relation.getId(),
                student.id(),
                student.name(),
                guardianId,
                null,
                relation.getRelationship(),
                relation.getIsEmergencyContact(),
                relation.getNotifyEvents(),
                relation.getCreatedAt()
        );
    }

    @Override
    protected StudentGuardianResponse core(GuardianStudentData data) {
        throw new UnsupportedOperationException("Use executeWithIds instead");
    }

    private StudentRequest toStudentRequest(GuardianStudentData data) {
        return new StudentRequest(
                data.name(),
                data.address(),
                data.homeLatitude(),
                data.homeLongitude(),
                data.schoolName(),
                data.schoolLatitude(),
                data.schoolLongitude(),
                data.grade(),
                data.birthDate(),
                data.emergencyContact(),
                data.emergencyPhone(),
                data.medicalInfo(),
                data.photoUrl(),
                data.studentCode()
        );
    }

    private boolean booleanOrDefault(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }
}
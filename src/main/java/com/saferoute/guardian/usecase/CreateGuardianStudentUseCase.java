package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.service.StudentService;
import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import com.saferoute.guardian.dto.GuardianStudentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Crea un estudiante y lo vincula al guardian automáticamente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateGuardianStudentUseCase extends UseCaseAdvance<GuardianStudentData, StudentGuardianResponse> {

    private final StudentService studentService;
    private final StudentGuardianService studentGuardianService;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    public StudentGuardianResponse executeWithGuardianId(UUID guardianId, GuardianStudentData data) {
        verifyOwnershipUseCase.execute(guardianId);
        
        StudentRequest studentRequest = toStudentRequest(data);
        StudentResponse student = studentService.create(studentRequest);

        StudentGuardianRequest relationRequest = new StudentGuardianRequest(
                student.id(),
                guardianId,
                data.relationship(),
                booleanOrDefault(data.isEmergencyContact(), false),
                booleanOrDefault(data.notifyEvents(), true)
        );
        
        StudentGuardianResponse relation = studentGuardianService.create(relationRequest);

        log.info("Created student {} for guardian {}", student.id(), guardianId);
        return new StudentGuardianResponse(
                relation.id(),
                student.id(),
                student.name(),
                relation.guardianId(),
                null,
                relation.relationship(),
                relation.isEmergencyContact(),
                relation.notifyEvents(),
                relation.createdAt()
        );
    }

    @Override
    protected StudentGuardianResponse core(GuardianStudentData data) {
        // No usado - usamos executeWithGuardianId
        throw new UnsupportedOperationException("Use executeWithGuardianId instead");
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
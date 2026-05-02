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
 * Crea un estudiante y lo vincula al guardian automáticamente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateGuardianStudentUseCase extends UseCaseAdvance<CreateGuardianStudentRequest, StudentGuardianResponse> {

    private final StudentService studentService;
    private final StudentGuardianRepository studentGuardianRepository;
    private final VerifyGuardianOwnershipUseCase verifyOwnershipUseCase;

    @Override
    protected StudentGuardianResponse core(CreateGuardianStudentRequest request) {
        // 1. Verificar ownership del guardian
        verifyOwnershipUseCase.execute(request.guardianId());

        // 2. Crear el estudiante
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
        StudentResponse student = studentService.create(studentRequest);

        // 3. Crear la relación
        StudentGuardianEntity relation = studentGuardianRepository.save(
                StudentGuardianEntity.builder()
                        .studentId(student.id())
                        .guardianId(request.guardianId())
                        .relationship(request.studentData().relationship())
                        .isEmergencyContact(booleanOrDefault(request.studentData().isEmergencyContact(), false))
                        .notifyEvents(booleanOrDefault(request.studentData().notifyEvents(), true))
                        .build()
        );

        log.info("Created student {} for guardian {}", student.id(), request.guardianId());
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

/**
 * Request record que contiene guardianId + datos del estudiante.
 */
record CreateGuardianStudentRequest(UUID guardianId, GuardianStudentData studentData) {}

/**
 * Datos del estudiante para crear/actualizar.
 */
record GuardianStudentData(
    String name,
    String address,
    Double homeLatitude,
    Double homeLongitude,
    String schoolName,
    Double schoolLatitude,
    Double schoolLongitude,
    String grade,
    java.time.LocalDate birthDate,
    String emergencyContact,
    String emergencyPhone,
    String medicalInfo,
    String photoUrl,
    String studentCode,
    com.saferoute.common.entity.StudentGuardianEntity.Relationship relationship,
    Boolean isEmergencyContact,
    Boolean notifyEvents
) {}
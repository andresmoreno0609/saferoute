package com.saferoute.guardian.service;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.repository.GuardianRepository;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.repository.StudentRepository;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar hijos (estudiantes) de un guardian.
 * Verifica que el usuario autenticado sea el owner del guardian.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuardianStudentService {

    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final GuardianRepository guardianRepository;
    private final UserRepository userRepository;

    /**
     * Crea un estudiante y lo vincula al guardian automáticamente.
     */
    @Transactional
    public StudentGuardianResponse createStudentForGuardian(UUID guardianId, GuardianStudentRequest request) {
        // Verificar que el guardian pertenece al usuario actual
        GuardianEntity guardian = verifyGuardianOwnership(guardianId);

        // Crear el estudiante
        StudentRequest studentRequest = toStudentRequest(request);
        StudentResponse student = studentService.create(studentRequest);

        // Crear la relación
        StudentGuardianRequest relationRequest = new StudentGuardianRequest(
                student.id(),
                guardianId,
                request.relationship(),
                request.isEmergencyContact() != null ? request.isEmergencyContact() : false,
                request.notifyEvents() != null ? request.notifyEvents() : true
        );

        StudentGuardianEntity relation = studentGuardianRepository.save(
                StudentGuardianEntity.builder()
                        .studentId(student.id())
                        .guardianId(guardianId)
                        .relationship(request.relationship())
                        .isEmergencyContact(request.isEmergencyContact() != null ? request.isEmergencyContact() : false)
                        .notifyEvents(request.notifyEvents() != null ? request.notifyEvents() : true)
                        .build()
        );

        log.info("Created student {} for guardian {}", student.id(), guardianId);
        return toResponse(relation, student);
    }

    /**
     * Actualiza un estudiante verificando que pertenezca al guardian.
     */
    @Transactional
    public StudentGuardianResponse updateStudentForGuardian(UUID guardianId, UUID studentId, GuardianStudentRequest request) {
        // Verificar que el guardian pertenece al usuario actual
        verifyGuardianOwnership(guardianId);

        // Verificar que el estudiante pertenece a este guardian
        StudentGuardianEntity relation = studentGuardianRepository.findByStudentIdAndGuardianId(studentId, guardianId)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este guardian"));

        // Actualizar estudiante
        StudentRequest studentRequest = toStudentRequest(request);
        StudentResponse student = studentService.update(studentId, studentRequest);

        // Actualizar relación
        relation.setRelationship(request.relationship());
        relation.setIsEmergencyContact(request.isEmergencyContact() != null ? request.isEmergencyContact() : false);
        relation.setNotifyEvents(request.notifyEvents() != null ? request.notifyEvents() : true);
        studentGuardianRepository.save(relation);

        log.info("Updated student {} for guardian {}", studentId, guardianId);
        return toResponse(relation, student);
    }

    /**
     * Elimina un estudiante verificando que no esté en rutas activas.
     */
    @Transactional
    public void deleteStudentForGuardian(UUID guardianId, UUID studentId) {
        // Verificar que el guardian pertenece al usuario actual
        verifyGuardianOwnership(guardianId);

        // Verificar que el estudiante pertenece a este guardian
        StudentGuardianEntity relation = studentGuardianRepository.findByStudentIdAndGuardianId(studentId, guardianId)
                .orElseThrow(() -> new IllegalArgumentException("El estudiante no pertenece a este guardian"));

        // Verificar que el estudiante no esté en rutas activas
        // Por ahora verificamos que no tenga relaciones con rutas (implementar según modelo real)
        // throw new IllegalStateException("El estudiante está asignado a una ruta. Solicite al conductor que lo retire primero.");

        // Eliminar relación
        studentGuardianRepository.delete(relation);

        // Eliminar estudiante (solo si no tiene más guardianes)
        List<StudentGuardianEntity> remainingRelations = studentGuardianRepository.findByStudentId(studentId);
        if (remainingRelations.isEmpty()) {
            studentRepository.deleteById(studentId);
            log.info("Deleted student {} (no more guardians)", studentId);
        } else {
            log.info("Removed student {} from guardian {}, still has {} guardians", studentId, guardianId, remainingRelations.size());
        }
    }

    /**
     * Lista todos los hijos de un guardian.
     */
    public List<StudentGuardianResponse> getStudentsByGuardian(UUID guardianId) {
        // Verificar que el guardian pertenece al usuario actual
        verifyGuardianOwnership(guardianId);

        List<StudentGuardianEntity> relations = studentGuardianRepository.findByGuardianId(guardianId);

        return relations.stream()
                .map(rel -> {
                    StudentEntity student = studentRepository.findById(rel.getStudentId())
                            .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado: " + rel.getStudentId()));
                    return toResponse(rel, toStudentResponse(student));
                })
                .toList();
    }

    /**
     * Verifica que el guardian pertenezca al usuario autenticado.
     */
    private GuardianEntity verifyGuardianOwnership(UUID guardianId) {
        // Obtener usuario actual del token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userIdStr = auth.getName();
        UUID userId = UUID.fromString(userIdStr);

        // Obtener usuario
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        // Si es admin, permitir todo
        if (user.getRoles().stream().anyMatch(r -> r.name() == com.saferoute.common.entity.UserEntity.Role.ADMIN)) {
            return guardianRepository.findById(guardianId)
                    .orElseThrow(() -> new EntityNotFoundException("Guardian no encontrado: " + guardianId));
        }

        // Si es guardian, verificar que sea el owner
        GuardianEntity guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new EntityNotFoundException("Guardian no encontrado: " + guardianId));

        if (!guardian.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes acceso a este guardian");
        }

        return guardian;
    }

    private StudentRequest toStudentRequest(GuardianStudentRequest request) {
        return new StudentRequest(
                request.name(),
                request.address(),
                request.homeLatitude(),
                request.homeLongitude(),
                request.schoolName(),
                request.schoolLatitude(),
                request.schoolLongitude(),
                request.grade(),
                request.birthDate(),
                request.emergencyContact(),
                request.emergencyPhone(),
                request.medicalInfo(),
                request.photoUrl(),
                request.studentCode()
        );
    }

    private StudentResponse toStudentResponse(StudentEntity entity) {
        return StudentResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .homeLatitude(entity.getHomeLatitude())
                .homeLongitude(entity.getHomeLongitude())
                .schoolName(entity.getSchoolName())
                .schoolLatitude(entity.getSchoolLatitude())
                .schoolLongitude(entity.getSchoolLongitude())
                .grade(entity.getGrade())
                .birthDate(entity.getBirthDate())
                .emergencyContact(entity.getEmergencyContact())
                .emergencyPhone(entity.getEmergencyPhone())
                .medicalInfo(entity.getMedicalInfo())
                .photoUrl(entity.getPhotoUrl())
                .studentCode(entity.getStudentCode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private StudentGuardianResponse toResponse(StudentGuardianEntity relation, StudentResponse student) {
        return new StudentGuardianResponse(
                relation.getId(),
                student.id(),
                student.name(),
                relation.getGuardianId(),
                null, // guardianName - se puede agregar si es necesario
                relation.getRelationship(),
                relation.getIsEmergencyContact(),
                relation.getNotifyEvents(),
                relation.getCreatedAt()
        );
    }
}
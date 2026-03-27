package com.saferoute.common.service;

import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.repository.GuardianRepository;
import com.saferoute.common.repository.StudentGuardianRepository;
import com.saferoute.common.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Student-Guardian relationship service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentGuardianService {

    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;

    public List<StudentGuardianResponse> findAll() {
        return studentGuardianRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentGuardianResponse findById(UUID id) {
        StudentGuardianEntity entity = studentGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional
    public StudentGuardianResponse create(StudentGuardianRequest request) {
        StudentEntity student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.studentId()));

        GuardianEntity guardian = guardianRepository.findById(request.guardianId())
                .orElseThrow(() -> new EntityNotFoundException("Guardian not found with id: " + request.guardianId()));

        // Check if relationship already exists
        if (studentGuardianRepository.existsByStudentIdAndGuardianId(request.studentId(), request.guardianId())) {
            throw new IllegalArgumentException("Relationship already exists between student and guardian");
        }

        StudentGuardianEntity entity = StudentGuardianEntity.builder()
                .student(student)
                .guardian(guardian)
                .relationship(request.relationship())
                .isEmergencyContact(request.isEmergencyContact() != null ? request.isEmergencyContact() : false)
                .notifyEvents(request.notifyEvents() != null ? request.notifyEvents() : true)
                .build();

        StudentGuardianEntity saved = studentGuardianRepository.save(entity);
        log.info("Student-Guardian relationship created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public StudentGuardianResponse update(UUID id, StudentGuardianRequest request) {
        StudentGuardianEntity entity = studentGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));

        if (request.relationship() != null) {
            entity.setRelationship(request.relationship());
        }
        if (request.isEmergencyContact() != null) {
            entity.setIsEmergencyContact(request.isEmergencyContact());
        }
        if (request.notifyEvents() != null) {
            entity.setNotifyEvents(request.notifyEvents());
        }

        StudentGuardianEntity saved = studentGuardianRepository.save(entity);
        log.info("Student-Guardian relationship updated: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        StudentGuardianEntity entity = studentGuardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Relationship not found with id: " + id));
        studentGuardianRepository.delete(entity);
        log.info("Student-Guardian relationship deleted: {}", id);
    }

    public List<StudentGuardianResponse> findByStudentId(UUID studentId) {
        return studentGuardianRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StudentGuardianResponse> findByGuardianId(UUID guardianId) {
        return studentGuardianRepository.findByGuardianId(guardianId).stream()
                .map(this::toResponse)
                .toList();
    }

    private StudentGuardianResponse toResponse(StudentGuardianEntity entity) {
        return new StudentGuardianResponse(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getStudent().getName(),
                entity.getGuardian().getId(),
                entity.getGuardian().getName(),
                entity.getRelationship(),
                entity.getIsEmergencyContact(),
                entity.getNotifyEvents(),
                entity.getCreatedAt()
        );
    }

    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }
}

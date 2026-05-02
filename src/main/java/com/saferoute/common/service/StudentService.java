package com.saferoute.common.service;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Student service with CRUD operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StudentResponse findById(UUID id) {
        StudentEntity entity = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));
        return toResponse(entity);
    }

    public StudentResponse create(StudentRequest request) {
        StudentEntity entity = StudentEntity.builder()
                .name(request.name())
                .address(request.address())
                .homeLatitude(request.homeLatitude())
                .homeLongitude(request.homeLongitude())
                .schoolName(request.schoolName())
                .schoolLatitude(request.schoolLatitude())
                .schoolLongitude(request.schoolLongitude())
                .grade(request.grade())
                .birthDate(request.birthDate())
                .emergencyContact(request.emergencyContact())
                .emergencyPhone(request.emergencyPhone())
                .medicalInfo(request.medicalInfo())
                .photoUrl(request.photoUrl())
                .studentCode(request.studentCode())
                .build();

        StudentEntity saved = studentRepository.save(entity);
        log.info("Student created: {}", saved.getId());
        return toResponse(saved);
    }

    public StudentResponse update(UUID id, StudentRequest request) {
        StudentEntity entity = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.address() != null) {
            entity.setAddress(request.address());
        }
        if (request.homeLatitude() != null) {
            entity.setHomeLatitude(request.homeLatitude());
        }
        if (request.homeLongitude() != null) {
            entity.setHomeLongitude(request.homeLongitude());
        }
        if (request.schoolName() != null) {
            entity.setSchoolName(request.schoolName());
        }
        if (request.schoolLatitude() != null) {
            entity.setSchoolLatitude(request.schoolLatitude());
        }
        if (request.schoolLongitude() != null) {
            entity.setSchoolLongitude(request.schoolLongitude());
        }
        if (request.grade() != null) {
            entity.setGrade(request.grade());
        }
        if (request.birthDate() != null) {
            entity.setBirthDate(request.birthDate());
        }
        if (request.emergencyContact() != null) {
            entity.setEmergencyContact(request.emergencyContact());
        }
        if (request.emergencyPhone() != null) {
            entity.setEmergencyPhone(request.emergencyPhone());
        }
        if (request.medicalInfo() != null) {
            entity.setMedicalInfo(request.medicalInfo());
        }
        if (request.photoUrl() != null) {
            entity.setPhotoUrl(request.photoUrl());
        }
        if (request.studentCode() != null) {
            entity.setStudentCode(request.studentCode());
        }

        StudentEntity saved = studentRepository.save(entity);
        log.info("Student updated: {}", id);
        return toResponse(saved);
    }

    public void delete(UUID id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
        log.info("Student deleted: {}", id);
    }

    private StudentResponse toResponse(StudentEntity entity) {
        return StudentResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .homeLatitude(entity.getHomeLatitude())
                .homeLongitude(entity.getHomeLongitude())
                .schoolName(entity.getSchoolName())
                .schoolLatitude(entity.getSchoolLatitude())
                .schoolLongitude(entity.getSchoolLongitude())
                .addressGeocoded(entity.getAddressGeocoded())
                .birthDate(entity.getBirthDate())
                .grade(entity.getGrade())
                .emergencyContact(entity.getEmergencyContact())
                .emergencyPhone(entity.getEmergencyPhone())
                .medicalInfo(entity.getMedicalInfo())
                .photoUrl(entity.getPhotoUrl())
                .studentCode(entity.getStudentCode())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Custom exception
    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }
}
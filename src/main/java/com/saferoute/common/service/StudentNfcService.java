package com.saferoute.common.service;

import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.entity.StudentNfcEntity;
import com.saferoute.common.repository.StudentNfcRepository;
import com.saferoute.common.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing Student NFC tags.
 * Only one active NFC per student at a time.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudentNfcService {

    private final StudentNfcRepository studentNfcRepository;
    private final StudentRepository studentRepository;

    /**
     * Get all NFCs for a student (including inactive)
     */
    public List<StudentNfcEntity> findByStudentId(UUID studentId) {
        return studentNfcRepository.findByStudentId(studentId);
    }

    /**
     * Get only active NFC for a student
     */
    public StudentNfcEntity getActiveByStudentId(UUID studentId) {
        return studentNfcRepository.findByStudentIdAndIsActiveTrue(studentId).orElse(null);
    }

    /**
     * Get NFC by UID
     */
    public StudentNfcEntity findByNfcUid(String nfcUid) {
        return studentNfcRepository.findByNfcUid(nfcUid).orElse(null);
    }

    /**
     * Get active NFC by UID
     */
    public StudentNfcEntity findActiveByNfcUid(String nfcUid) {
        return studentNfcRepository.findByNfcUidAndIsActiveTrue(nfcUid).orElse(null);
    }

    /**
     * Assign a new NFC to a student.
     * If student already has an active NFC, it will be deactivated.
     * If NFC UID already exists (assigned to another student), return error.
     */
    @Transactional
    public StudentNfcEntity assignNfc(UUID studentId, String nfcUid, UUID assignedBy, String notes) {
        // Verify student exists
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNfcNotFoundException("Student not found with id: " + studentId));

        // Check if NFC UID already exists
        if (studentNfcRepository.existsByNfcUid(nfcUid)) {
            throw new NfcAlreadyAssignedException("NFC UID " + nfcUid + " is already assigned to another student");
        }

        // Deactivate current active NFC if exists
        studentNfcRepository.findByStudentIdAndIsActiveTrue(studentId)
                .ifPresent(currentNfc -> {
                    currentNfc.setIsActive(false);
                    currentNfc.setDeactivatedAt(LocalDateTime.now());
                    studentNfcRepository.save(currentNfc);
                    log.info("Deactivated previous NFC {} for student {}", currentNfc.getNfcUid(), studentId);
                });

        // Create new NFC record
        StudentNfcEntity newNfc = StudentNfcEntity.builder()
                .student(student)
                .nfcUid(nfcUid)
                .isActive(true)
                .assignedBy(assignedBy)
                .notes(notes)
                .build();

        StudentNfcEntity saved = studentNfcRepository.save(newNfc);
        log.info("Assigned NFC {} to student {}", nfcUid, studentId);
        return saved;
    }

    /**
     * Deactivate NFC for a student (without assigning a new one)
     */
    @Transactional
    public void deactivateNfc(UUID studentId) {
        StudentNfcEntity activeNfc = studentNfcRepository.findByStudentIdAndIsActiveTrue(studentId)
                .orElseThrow(() -> new NfcNotFoundException("No active NFC found for student " + studentId));

        activeNfc.setIsActive(false);
        activeNfc.setDeactivatedAt(LocalDateTime.now());
        studentNfcRepository.save(activeNfc);
        log.info("Deactivated NFC {} for student {}", activeNfc.getNfcUid(), studentId);
    }

    /**
     * Get all active NFCs (for scanning/detection)
     */
    public List<StudentNfcEntity> getAllActiveNfc() {
        return studentNfcRepository.findAllActive();
    }

    /**
     * Check if student has an active NFC
     */
    public boolean hasActiveNfc(UUID studentId) {
        return studentNfcRepository.existsByStudentIdAndIsActiveTrue(studentId);
    }

    // Exceptions
    public static class StudentNfcNotFoundException extends RuntimeException {
        public StudentNfcNotFoundException(String message) {
            super(message);
        }
    }

    public static class NfcNotFoundException extends RuntimeException {
        public NfcNotFoundException(String message) {
            super(message);
        }
    }

    public static class NfcAlreadyAssignedException extends RuntimeException {
        public NfcAlreadyAssignedException(String message) {
            super(message);
        }
    }
}
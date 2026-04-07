package com.saferoute.common.repository;

import com.saferoute.common.entity.StudentNfcEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentNfcRepository extends JpaRepository<StudentNfcEntity, UUID> {

    /**
     * Find all NFC records for a student (including inactive)
     */
    List<StudentNfcEntity> findByStudentId(UUID studentId);

    /**
     * Find only active NFC for a student
     */
    Optional<StudentNfcEntity> findByStudentIdAndIsActiveTrue(UUID studentId);

    /**
     * Find by NFC UID
     */
    Optional<StudentNfcEntity> findByNfcUid(String nfcUid);

    /**
     * Find active NFC by UID
     */
    Optional<StudentNfcEntity> findByNfcUidAndIsActiveTrue(String nfcUid);

    /**
     * Check if NFC UID exists
     */
    boolean existsByNfcUid(String nfcUid);

    /**
     * Check if student has an active NFC
     */
    boolean existsByStudentIdAndIsActiveTrue(UUID studentId);

    /**
     * Find all active NFCs (for scanning)
     */
    @Query("SELECT n FROM StudentNfcEntity n WHERE n.isActive = true")
    List<StudentNfcEntity> findAllActive();
}
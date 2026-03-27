package com.saferoute.common.repository;

import com.saferoute.common.entity.StudentGuardianEntity;
import com.saferoute.common.entity.StudentGuardianEntity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentGuardianRepository extends JpaRepository<StudentGuardianEntity, UUID>, JpaSpecificationExecutor<StudentGuardianEntity> {
    
    List<StudentGuardianEntity> findByStudentId(UUID studentId);
    
    List<StudentGuardianEntity> findByGuardianId(UUID guardianId);
    
    Optional<StudentGuardianEntity> findByStudentIdAndGuardianId(UUID studentId, UUID guardianId);
    
    boolean existsByStudentIdAndGuardianId(UUID studentId, UUID guardianId);
    
    List<StudentGuardianEntity> findByStudentIdAndNotifyEventsTrue(UUID studentId);
    
    List<StudentGuardianEntity> findByStudentIdAndIsEmergencyContactTrue(UUID studentId);
    
    boolean existsByStudentIdAndIsEmergencyContactTrue(UUID studentId);
}

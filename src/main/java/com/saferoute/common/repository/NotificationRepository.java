package com.saferoute.common.repository;

import com.saferoute.common.entity.NotificationEntity;
import com.saferoute.common.entity.NotificationEntity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID>, JpaSpecificationExecutor<NotificationEntity> {
    
    List<NotificationEntity> findByGuardianIdOrderBySentAtDesc(UUID guardianId);
    
    List<NotificationEntity> findByGuardianIdAndReadAtIsNullOrderBySentAtDesc(UUID guardianId);
    
    long countByGuardianIdAndReadAtIsNull(UUID guardianId);
}

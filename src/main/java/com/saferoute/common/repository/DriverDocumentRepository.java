package com.saferoute.common.repository;

import com.saferoute.common.entity.DriverDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverDocumentRepository extends JpaRepository<DriverDocumentEntity, UUID>, JpaSpecificationExecutor<DriverDocumentEntity> {
    
    List<DriverDocumentEntity> findByDriverId(UUID driverId);
    
    List<DriverDocumentEntity> findByDriverIdAndIsActiveTrue(UUID driverId);
    
    Optional<DriverDocumentEntity> findByDriverIdAndDocumentTypeAndIsActiveTrue(UUID driverId, DriverDocumentEntity.DriverDocumentType documentType);
    
    Optional<DriverDocumentEntity> findByDriverIdAndDocumentType(UUID driverId, DriverDocumentEntity.DriverDocumentType documentType);
    
    @Query("SELECT dd FROM DriverDocumentEntity dd WHERE dd.driver.id = :driverId AND dd.documentType = :documentType AND dd.isActive = true")
    Optional<DriverDocumentEntity> findActiveByDriverAndType(UUID driverId, DriverDocumentEntity.DriverDocumentType documentType);
}
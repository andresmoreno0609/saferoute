package com.saferoute.common.repository;

import com.saferoute.common.entity.VehicleDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleDocumentRepository extends JpaRepository<VehicleDocumentEntity, UUID>, JpaSpecificationExecutor<VehicleDocumentEntity> {
    
    List<VehicleDocumentEntity> findByVehicleId(UUID vehicleId);
    
    List<VehicleDocumentEntity> findByVehicleIdAndIsActiveTrue(UUID vehicleId);
    
    Optional<VehicleDocumentEntity> findByVehicleIdAndDocumentTypeAndIsActiveTrue(UUID vehicleId, VehicleDocumentEntity.VehicleDocumentType documentType);
    
    Optional<VehicleDocumentEntity> findByVehicleIdAndDocumentType(UUID vehicleId, VehicleDocumentEntity.VehicleDocumentType documentType);
    
    @Query("SELECT vd FROM VehicleDocumentEntity vd WHERE vd.vehicle.id = :vehicleId AND vd.documentType = :documentType AND vd.isActive = true")
    Optional<VehicleDocumentEntity> findActiveByVehicleAndType(UUID vehicleId, VehicleDocumentEntity.VehicleDocumentType documentType);
}
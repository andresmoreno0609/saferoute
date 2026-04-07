package com.saferoute.common.repository;

import com.saferoute.common.entity.DriverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, UUID>, JpaSpecificationExecutor<DriverEntity> {
    
    Optional<DriverEntity> findByUserId(UUID userId);
    
    Optional<DriverEntity> findByVehicle_Id(UUID vehicleId);
    
    boolean existsByVehicle_Id(UUID vehicleId);
    
    boolean existsByUserId(UUID userId);
}

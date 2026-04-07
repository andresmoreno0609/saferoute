package com.saferoute.common.repository;

import com.saferoute.common.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID>, JpaSpecificationExecutor<VehicleEntity> {
    
    Optional<VehicleEntity> findByPlate(String plate);
    
    boolean existsByPlate(String plate);
    
    Optional<VehicleEntity> findByDriverId(UUID driverId);
    
    boolean existsByDriverId(UUID driverId);
}
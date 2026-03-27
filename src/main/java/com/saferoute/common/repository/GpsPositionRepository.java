package com.saferoute.common.repository;

import com.saferoute.common.entity.GpsPositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GpsPositionRepository extends JpaRepository<GpsPositionEntity, UUID>, JpaSpecificationExecutor<GpsPositionEntity> {
    
    List<GpsPositionEntity> findByRouteIdOrderByTimestampDesc(UUID routeId);
    
    List<GpsPositionEntity> findByDriverIdOrderByTimestampDesc(UUID driverId);
    
    Optional<GpsPositionEntity> findFirstByRouteIdOrderByTimestampDesc(UUID routeId);
}

package com.saferoute.common.repository;

import com.saferoute.common.entity.RouteEntity;
import com.saferoute.common.entity.RouteEntity.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, UUID>, JpaSpecificationExecutor<RouteEntity> {
    
    List<RouteEntity> findByDriverId(UUID driverId);
    
    List<RouteEntity> findByStatus(RouteStatus status);
    
    List<RouteEntity> findByScheduledDate(LocalDate scheduledDate);
    
    Optional<RouteEntity> findByDriverIdAndStatus(UUID driverId, RouteStatus status);
    
    boolean existsByDriverIdAndStatus(UUID driverId, RouteStatus status);
    
    List<RouteEntity> findByDriverIdAndScheduledDate(UUID driverId, LocalDate scheduledDate);
}

package com.saferoute.common.repository;

import com.saferoute.common.entity.ObservationEntity;
import com.saferoute.common.entity.ObservationEntity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObservationRepository extends JpaRepository<ObservationEntity, UUID>, JpaSpecificationExecutor<ObservationEntity> {
    
    List<ObservationEntity> findByStudentIdOrderByTimestampDesc(UUID studentId);
    
    List<ObservationEntity> findByDriverIdOrderByTimestampDesc(UUID driverId);
    
    List<ObservationEntity> findByRouteIdOrderByTimestampDesc(UUID routeId);
    
    List<ObservationEntity> findBySeverity(Severity severity);
}

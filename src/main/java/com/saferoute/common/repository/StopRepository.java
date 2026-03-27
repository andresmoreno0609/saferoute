package com.saferoute.common.repository;

import com.saferoute.common.entity.StopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StopRepository extends JpaRepository<StopEntity, UUID>, JpaSpecificationExecutor<StopEntity> {
    
    List<StopEntity> findByRouteIdOrderByOrderNumAsc(UUID routeId);
    
    List<StopEntity> findByStudentId(UUID studentId);
    
    Optional<StopEntity> findByRouteIdAndStudentId(UUID routeId, UUID studentId);
    
    Optional<StopEntity> findByRouteIdAndOrderNum(UUID routeId, Integer orderNum);
    
    boolean existsByRouteIdAndStudentId(UUID routeId, UUID studentId);
    
    boolean existsByRouteIdAndOrderNum(UUID routeId, Integer orderNum);
}

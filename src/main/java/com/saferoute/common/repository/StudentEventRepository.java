package com.saferoute.common.repository;

import com.saferoute.common.entity.StudentEventEntity;
import com.saferoute.common.entity.StudentEventEntity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentEventRepository extends JpaRepository<StudentEventEntity, UUID>, JpaSpecificationExecutor<StudentEventEntity> {
    
    List<StudentEventEntity> findByStudentIdOrderByTimestampDesc(UUID studentId);
    
    List<StudentEventEntity> findByRouteIdOrderByTimestampDesc(UUID routeId);
    
    Optional<StudentEventEntity> findFirstByStudentIdAndRouteIdOrderByTimestampDesc(UUID studentId, UUID routeId);
    
    List<StudentEventEntity> findByStudentIdAndEventType(UUID studentId, EventType eventType);
}

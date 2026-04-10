package com.saferoute.common.repository;

import com.saferoute.common.entity.GuardianEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuardianRepository extends JpaRepository<GuardianEntity, UUID>, JpaSpecificationExecutor<GuardianEntity> {
    
    Optional<GuardianEntity> findByPhone(String phone);
    
    Optional<GuardianEntity> findByEmail(String email);
    
    Optional<GuardianEntity> findByUserId(UUID userId);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByUserId(UUID userId);
}

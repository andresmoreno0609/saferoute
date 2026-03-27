package com.saferoute.common.service;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.repository.GuardianRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuardianService extends BaseCrudService<GuardianEntity, GuardianRequest, GuardianResponse, UUID> {

    private final GuardianRepository guardianRepository;

    @Override
    protected GuardianRepository getRepository() {
        return guardianRepository;
    }

    @Override
    protected GuardianEntity toEntity(GuardianRequest request) {
        return GuardianEntity.builder()
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .fcmToken(request.fcmToken())
                .build();
    }

    @Override
    protected GuardianResponse toResponse(GuardianEntity entity) {
        return new GuardianResponse(
                entity.getId(),
                entity.getName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getFcmToken(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    protected void updateEntity(GuardianRequest request, GuardianEntity entity) {
        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.phone() != null) {
            entity.setPhone(request.phone());
        }
        if (request.email() != null) {
            entity.setEmail(request.email());
        }
        if (request.fcmToken() != null) {
            entity.setFcmToken(request.fcmToken());
        }
    }

    public void updateFcmToken(UUID guardianId, String fcmToken) {
        GuardianEntity guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new EntityNotFoundException("Guardian not found: " + guardianId));
        guardian.setFcmToken(fcmToken);
        guardianRepository.save(guardian);
    }
}

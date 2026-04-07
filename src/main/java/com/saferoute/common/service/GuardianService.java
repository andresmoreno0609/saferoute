package com.saferoute.common.service;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.repository.GuardianRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .documentNumber(request.documentNumber())
                .birthDate(request.birthDate())
                .address(request.address())
                .photoUrl(request.photoUrl())
                .emergencyContact(request.emergencyContact())
                .emergencyPhone(request.emergencyPhone())
                .occupation(request.occupation())
                .workPhone(request.workPhone())
                .build();
    }

    @Override
    protected GuardianResponse toResponse(GuardianEntity entity) {
        return GuardianResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .fcmToken(entity.getFcmToken())
                .documentNumber(entity.getDocumentNumber())
                .birthDate(entity.getBirthDate())
                .address(entity.getAddress())
                .photoUrl(entity.getPhotoUrl())
                .emergencyContact(entity.getEmergencyContact())
                .emergencyPhone(entity.getEmergencyPhone())
                .occupation(entity.getOccupation())
                .workPhone(entity.getWorkPhone())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
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
        if (request.documentNumber() != null) {
            entity.setDocumentNumber(request.documentNumber());
        }
        if (request.birthDate() != null) {
            entity.setBirthDate(request.birthDate());
        }
        if (request.address() != null) {
            entity.setAddress(request.address());
        }
        if (request.photoUrl() != null) {
            entity.setPhotoUrl(request.photoUrl());
        }
        if (request.emergencyContact() != null) {
            entity.setEmergencyContact(request.emergencyContact());
        }
        if (request.emergencyPhone() != null) {
            entity.setEmergencyPhone(request.emergencyPhone());
        }
        if (request.occupation() != null) {
            entity.setOccupation(request.occupation());
        }
        if (request.workPhone() != null) {
            entity.setWorkPhone(request.workPhone());
        }
    }

    public void updateFcmToken(UUID guardianId, String fcmToken) {
        GuardianEntity guardian = guardianRepository.findById(guardianId)
                .orElseThrow(() -> new EntityNotFoundException("Guardian not found: " + guardianId));
        guardian.setFcmToken(fcmToken);
        guardianRepository.save(guardian);
    }
}

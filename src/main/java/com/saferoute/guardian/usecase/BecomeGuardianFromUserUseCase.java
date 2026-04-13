package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.repository.GuardianRepository;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.usecase.UseCaseAdvance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for a user to become a guardian.
 * This endpoint allows an existing user to add the GUARDIAN role and create a guardian profile.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BecomeGuardianFromUserUseCase extends UseCaseAdvance<BecomeGuardianFromUserRequest, GuardianResponse> {

    private final UserRepository userRepository;
    private final GuardianRepository guardianRepository;

    @Override
    protected void preConditions(BecomeGuardianFromUserRequest request) {
        // 1. Validate user exists
        if (!userRepository.existsById(request.userId())) {
            throw new EntityNotFoundException("User not found with id: " + request.userId());
        }

        // 2. Validate user does NOT have GUARDIAN role
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.userId()));
        
        if (user.hasRole(UserRole.GUARDIAN)) {
            throw new IllegalStateException("User already has GUARDIAN role");
        }

        // 3. Validate user does NOT already have a guardian profile
        if (guardianRepository.existsByUserId(request.userId())) {
            throw new IllegalStateException("User already has a guardian profile");
        }
    }

    @Override
    @Transactional
    protected GuardianResponse core(BecomeGuardianFromUserRequest request) {
        log.debug("User {} becoming a guardian", request.userId());

        // Get user entity
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + request.userId()));

        // Add GUARDIAN role to user
        user.addRole(UserRole.GUARDIAN);
        userRepository.save(user);
        log.info("Added GUARDIAN role to user: {}", request.userId());

        // Create guardian profile with userId
        GuardianEntity guardian = GuardianEntity.builder()
                .user(user)
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .documentNumber(request.documentNumber())
                .address(request.address())
                .emergencyContact(request.emergencyContact())
                .emergencyPhone(request.emergencyPhone())
                .occupation(request.occupation())
                .workPhone(request.workPhone())
                .build();

        GuardianEntity saved = guardianRepository.save(guardian);
        log.info("Guardian profile created for user: {}", request.userId());

        return toResponse(saved);
    }

    private GuardianResponse toResponse(GuardianEntity entity) {
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
}
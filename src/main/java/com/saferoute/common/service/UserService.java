package com.saferoute.common.service;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * User service implementing BaseCrudService for standard CRUD operations.
 * Provides additional methods specific to user management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService extends BaseCrudService<UserEntity, UserRequest, UserResponse, UUID> {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // ==================== ABSTRACT METHODS ====================

    @Override
    protected UserRepository getRepository() {
        return userRepository;
    }

    @Override
    protected UserEntity toEntity(UserRequest request) {
        return UserEntity.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(request.role())
                .status(request.status() != null ? request.status() : UserStatus.ACTIVE)
                .build();
    }

    @Override
    protected UserResponse toResponse(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getName(),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getLastLoginAt()
        );
    }

    @Override
    protected void updateEntity(UserRequest request, UserEntity entity) {
        if (request.email() != null) {
            entity.setEmail(request.email());
        }
        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.role() != null) {
            entity.setRole(request.role());
        }
        if (request.status() != null) {
            entity.setStatus(request.status());
        }
        // Password is handled separately for security
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
    }

    // ==================== CUSTOM METHODS ====================

    /**
     * Finds a user by email.
     */
    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    /**
     * Checks if a user exists by email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Updates the last login timestamp.
     */
    public void updateLastLogin(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(java.time.LocalDateTime.now());
            userRepository.save(user);
        });
    }
}

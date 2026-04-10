package com.saferoute.common.service;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
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
                .roles(request.roles() != null ? request.roles() : Set.of())
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Override
    protected UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .roles(entity.getRoles())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .lastLoginAt(entity.getLastLoginAt())
                .build();
    }

    @Override
    protected void updateEntity(UserRequest request, UserEntity entity) {
        if (request.email() != null) {
            entity.setEmail(request.email());
        }
        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.roles() != null && !request.roles().isEmpty()) {
            entity.setRoles(request.roles());
        }
        if (request.email() != null) {  // Keep status handling
            entity.setStatus(UserStatus.ACTIVE);
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

    /**
     * Add a role to a user.
     */
    public void addRole(UUID userId, UserRole role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        user.addRole(role);
        userRepository.save(user);
    }

    /**
     * Remove a role from a user.
     */
    public void removeRole(UUID userId, UserRole role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        user.removeRole(role);
        userRepository.save(user);
    }

    /**
     * Check if user has a specific role.
     */
    public boolean hasRole(UUID userId, UserRole role) {
        return userRepository.findById(userId)
                .map(user -> user.hasRole(role))
                .orElse(false);
    }
}

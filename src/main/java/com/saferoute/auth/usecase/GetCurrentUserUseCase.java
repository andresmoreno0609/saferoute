package com.saferoute.auth.usecase;

import com.saferoute.common.dto.auth.UserInfoResponse;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for getting current authenticated user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetCurrentUserUseCase extends UseCaseAdvance<Void, UserInfoResponse> {

    private final UserRepository userRepository;

    @Override
    protected UserInfoResponse core(Void request) {
        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Get user ID from authentication
        String userId = authentication.getName();
        UUID uuid = UUID.fromString(userId);

        // Find user
        UserEntity user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        // Build response
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();

        return new UserInfoResponse(userResponse);
    }
}
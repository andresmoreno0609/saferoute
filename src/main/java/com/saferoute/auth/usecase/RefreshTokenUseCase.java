package com.saferoute.auth.usecase;

import com.saferoute.common.dto.auth.AuthResponse;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.service.JwtService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case for refreshing access token.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase extends UseCaseAdvance<String, AuthResponse> {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    protected void preConditions(String refreshToken) {
        // Validate refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Check if token is expired
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new BadCredentialsException("Refresh token expired");
        }
    }

    @Override
    protected AuthResponse core(String refreshToken) {
        // Extract user info from refresh token
        var userId = jwtService.extractUserId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);

        // Find user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Get roles as Set<String>
        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Generate new access token with roles
        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                roleNames
        );

        // Generate new refresh token
        String newRefreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                roleNames
        );

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

        log.info("Token refreshed for user: {}", email);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                userResponse
        );
    }
}

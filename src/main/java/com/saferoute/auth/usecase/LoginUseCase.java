package com.saferoute.auth.usecase;

import com.saferoute.common.dto.auth.AuthLoginRequest;
import com.saferoute.common.dto.auth.AuthResponse;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.service.JwtService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case for user login.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase extends UseCaseAdvance<AuthLoginRequest, AuthResponse> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void preConditions(AuthLoginRequest request) {
        // Validate that user exists
        userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
    }

    @Override
    protected AuthResponse core(AuthLoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Validate password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Check if user is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        // Get roles as Set<String>
        Set<String> roleNames = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Generate tokens with roles
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                roleNames
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                roleNames
        );

        // Update last login
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

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

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                userResponse
        );
    }
}

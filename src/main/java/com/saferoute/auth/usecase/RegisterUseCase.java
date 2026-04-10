package com.saferoute.auth.usecase;

import com.saferoute.common.dto.auth.AuthRegisterRequest;
import com.saferoute.common.dto.auth.AuthResponse;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.service.JwtService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Use case for user registration.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterUseCase extends UseCaseAdvance<AuthRegisterRequest, AuthResponse> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void preConditions(AuthRegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
    }

    @Override
    protected AuthResponse core(AuthRegisterRequest request) {
        // Create new user with roles
        Set<UserEntity.UserRole> roles = request.roles() != null && !request.roles().isEmpty()
                ? request.roles()
                : Set.of(UserEntity.UserRole.GUARDIAN);  // Default role
        
        UserEntity user = UserEntity.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .roles(roles)
                .status(UserStatus.ACTIVE)
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        // Get roles as Set<String>
        Set<String> roleNames = savedUser.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Generate tokens with roles
        String accessToken = jwtService.generateAccessToken(
                savedUser.getId(),
                savedUser.getEmail(),
                roleNames
        );

        String refreshToken = jwtService.generateRefreshToken(
                savedUser.getId(),
                savedUser.getEmail(),
                roleNames
        );

        // Build response
        UserResponse userResponse = UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .roles(savedUser.getRoles())
                .status(savedUser.getStatus())
                .createdAt(savedUser.getCreatedAt())
                .lastLoginAt(savedUser.getLastLoginAt())
                .build();

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                userResponse
        );
    }
}

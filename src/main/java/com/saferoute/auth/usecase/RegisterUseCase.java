package com.saferoute.auth.usecase;

import com.saferoute.common.dto.auth.AuthRegisterRequest;
import com.saferoute.common.dto.auth.AuthResponse;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.entity.GuardianEntity;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.GuardianRepository;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.service.JwtService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    private final GuardianRepository guardianRepository;
    private final DriverRepository driverRepository;
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
    @Transactional
    protected AuthResponse core(AuthRegisterRequest request) {
        // Create new user with roles
        Set<UserRole> roles = request.roles() != null && !request.roles().isEmpty()
                ? request.roles()
                : Set.of(UserRole.GUARDIAN);  // Default role

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .roles(roles)
                .status(UserStatus.ACTIVE)
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        // Create profile based on roles
        createUserProfile(savedUser, roles);

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

    /**
     * Create user profile based on roles (Guardian or Driver).
     */
    private void createUserProfile(UserEntity user, Set<UserRole> roles) {
        if (roles.contains(UserRole.GUARDIAN)) {
            createGuardianProfile(user);
        }
        if (roles.contains(UserRole.DRIVER)) {
            createDriverProfile(user);
        }
    }

    /**
     * Create guardian profile with basic data from user.
     */
    private void createGuardianProfile(UserEntity user) {
        GuardianEntity guardian = GuardianEntity.builder()
                .user(user)
                .name(user.getName())
                .email(user.getEmail())
                .phone(null)  // Can be updated later
                .build();

        guardianRepository.save(guardian);
        log.info("Guardian profile created for user: {}", user.getEmail());
    }

    /**
     * Create driver profile with basic data from user.
     */
    private void createDriverProfile(UserEntity user) {
        DriverEntity driver = DriverEntity.builder()
                .user(user)
                .name(user.getName())
                .phone(null)  // Can be updated later
                .build();

        driverRepository.save(driver);
        log.info("Driver profile created for user: {}", user.getEmail());
    }
}
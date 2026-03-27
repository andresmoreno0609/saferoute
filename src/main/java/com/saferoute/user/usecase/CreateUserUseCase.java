package com.saferoute.user.usecase;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.entity.UserEntity.UserStatus;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.service.UserService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Use case for creating a new user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCase extends UseCaseAdvance<UserRequest, UserResponse> {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void preConditions(UserRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
    }

    @Override
    protected UserResponse core(UserRequest request) {
        log.debug("Creating new user: {}", request.email());

        UserResponse created = userService.create(request);
        log.info("User created successfully: {}", created.id());
        
        return created;
    }
}

package com.saferoute.user.usecase;

import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.service.UserService;
import com.saferoute.common.usecase.UseCaseAdvance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for getting a user by ID.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetUserByIdUseCase extends UseCaseAdvance<UUID, UserResponse> {

    private final UserService userService;

    @Override
    protected void preConditions(UUID id) {
        if (!userService.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    protected UserResponse core(UUID id) {
        log.debug("Fetching user by id: {}", id);
        return userService.findById(id);
    }
}

package com.saferoute.user.usecase;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.common.service.UserService;
import com.saferoute.common.usecase.UseCaseAdvance;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for updating a user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserUseCase extends UseCaseAdvance<UpdateUserRequest, UserResponse> {

    private final UserService userService;

    @Override
    protected void preConditions(UpdateUserRequest request) {
        if (!userService.existsById(request.id())) {
            throw new EntityNotFoundException("User not found with id: " + request.id());
        }
    }

    @Override
    protected UserResponse core(UpdateUserRequest request) {
        log.debug("Updating user: {}", request.id());
        
        UserResponse updated = userService.update(request.id(), request.request());
        log.info("User updated successfully: {}", request.id());
        
        return updated;
    }
}

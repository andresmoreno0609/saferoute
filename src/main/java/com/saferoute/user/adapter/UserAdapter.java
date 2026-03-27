package com.saferoute.user.adapter;

import com.saferoute.common.dto.user.UserRequest;
import com.saferoute.common.dto.user.UserResponse;
import com.saferoute.user.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * User Adapter - orchestrates use cases for user management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserAdapter {

    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    /**
     * Get all users.
     */
    public List<UserResponse> getAll() {
        return getAllUsersUseCase.execute(null);
    }

    /**
     * Get user by ID.
     */
    public UserResponse getById(UUID id) {
        return getUserByIdUseCase.execute(id);
    }

    /**
     * Create a new user.
     */
    public UserResponse create(UserRequest request) {
        return createUserUseCase.execute(request);
    }

    /**
     * Update an existing user.
     */
    public UserResponse update(UUID id, UserRequest request) {
        return updateUserUseCase.execute(
            new UpdateUserRequest(id, request)
        );
    }

    /**
     * Delete a user.
     */
    public void delete(UUID id) {
        deleteUserUseCase.execute(id);
    }
}

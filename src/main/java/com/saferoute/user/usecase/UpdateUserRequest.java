package com.saferoute.user.usecase;

import com.saferoute.common.dto.user.UserRequest;

import java.util.UUID;

/**
 * Request record for updating a user.
 */
public record UpdateUserRequest(UUID id, UserRequest request) {}

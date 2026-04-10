package com.saferoute.common.dto.user;

import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.entity.UserEntity.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String email,
    String name,
    Set<UserRole> roles,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt
) {}

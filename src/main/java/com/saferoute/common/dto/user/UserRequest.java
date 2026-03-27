package com.saferoute.common.dto.user;

import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.entity.UserEntity.UserStatus;

public record UserRequest(
    String email,
    String password,
    String name,
    UserRole role,
    UserStatus status
) {}

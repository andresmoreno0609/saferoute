package com.saferoute.common.dto.auth;

import com.saferoute.common.dto.user.UserResponse;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    UserResponse user
) {}

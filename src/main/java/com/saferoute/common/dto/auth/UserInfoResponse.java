package com.saferoute.common.dto.auth;

import com.saferoute.common.dto.user.UserResponse;

/**
 * Response for /me endpoint - only user info, no tokens.
 */
public record UserInfoResponse(
    UserResponse user
) {}
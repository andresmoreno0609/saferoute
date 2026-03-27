package com.saferoute.auth.adapter;

import com.saferoute.auth.usecase.*;
import com.saferoute.common.dto.auth.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Auth Adapter - orchestrates use cases for authentication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAdapter {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    /**
     * Authenticate user with credentials.
     */
    public AuthResponse login(AuthLoginRequest request) {
        return loginUseCase.execute(request);
    }

    /**
     * Register a new user.
     */
    public AuthResponse register(AuthRegisterRequest request) {
        return registerUseCase.execute(request);
    }

    /**
     * Refresh access token.
     */
    public AuthResponse refreshToken(String refreshToken) {
        return refreshTokenUseCase.execute(refreshToken);
    }

    /**
     * Logout user.
     */
    public void logout() {
        logoutUseCase.execute(null);
    }

    /**
     * Get current authenticated user.
     */
    public AuthResponse getCurrentUser() {
        return getCurrentUserUseCase.execute(null);
    }
}

package com.saferoute.auth.usecase;

import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for user logout.
 * Note: In JWT stateless authentication, logout is handled client-side
 * by removing tokens. This use case can be extended to implement
 * token blacklist if needed.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCase extends UseCaseAdvance<Void, Void> {

    @Override
    protected Void core(Void request) {
        // In a stateless JWT setup, logout is handled by the client
        // by removing the access and refresh tokens from storage.
        //
        // For enhanced security, consider implementing:
        // 1. Token blacklist in Redis/database
        // 2. Short-lived access tokens
        // 3. Token version/user session tracking
        //
        // For now, we just log the logout event.
        log.info("User logged out successfully");
        return null;
    }
}

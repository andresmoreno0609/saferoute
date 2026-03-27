package com.saferoute.guardian.usecase;

import java.util.UUID;

/**
 * Request record for updating FCM token.
 */
public record UpdateFcmTokenRequest(UUID id, String token) {}

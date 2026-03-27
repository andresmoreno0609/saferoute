package com.saferoute.common.dto.guardian;

import java.time.LocalDateTime;
import java.util.UUID;

public record GuardianResponse(
    UUID id,
    String name,
    String phone,
    String email,
    String fcmToken,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

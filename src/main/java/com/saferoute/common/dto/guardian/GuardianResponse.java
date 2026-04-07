package com.saferoute.common.dto.guardian;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record GuardianResponse(
    UUID id,
    String name,
    String phone,
    String email,
    String fcmToken,
    String documentNumber,
    LocalDate birthDate,
    String address,
    String photoUrl,
    String emergencyContact,
    String emergencyPhone,
    String occupation,
    String workPhone,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

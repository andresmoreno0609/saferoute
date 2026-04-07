package com.saferoute.common.dto.student;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record StudentResponse(
    UUID id,
    String name,
    String address,
    Double homeLatitude,
    Double homeLongitude,
    String schoolName,
    Double schoolLatitude,
    Double schoolLongitude,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

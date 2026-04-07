package com.saferoute.common.dto.student;

import lombok.Builder;

import java.time.LocalDate;
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
    Boolean addressGeocoded,
    LocalDate birthDate,
    String grade,
    String emergencyContact,
    String emergencyPhone,
    String medicalInfo,
    String photoUrl,
    String studentCode,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

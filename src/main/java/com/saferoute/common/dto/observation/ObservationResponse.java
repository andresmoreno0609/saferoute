package com.saferoute.common.dto.observation;

import com.saferoute.common.entity.ObservationEntity.Severity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ObservationResponse(
    UUID id,
    UUID studentId,
    String studentName,
    UUID driverId,
    String driverName,
    UUID routeId,
    String routeName,
    String description,
    String photoUrl,
    Severity severity,
    LocalDateTime timestamp,
    LocalDateTime createdAt
) {}

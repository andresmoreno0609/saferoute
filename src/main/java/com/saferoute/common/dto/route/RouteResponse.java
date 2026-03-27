package com.saferoute.common.dto.route;

import com.saferoute.common.entity.RouteEntity.RouteStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RouteResponse(
    UUID id,
    String name,
    UUID driverId,
    String driverName,
    RouteStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    LocalDate scheduledDate,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

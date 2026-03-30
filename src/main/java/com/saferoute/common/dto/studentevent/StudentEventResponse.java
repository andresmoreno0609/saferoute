package com.saferoute.common.dto.studentevent;

import com.saferoute.common.entity.StudentEventEntity.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentEventResponse(
    UUID id,
    UUID studentId,
    String studentName,
    UUID driverId,
    String driverName,
    UUID routeId,
    String routeName,
    EventType eventType,
    Double latitude,
    Double longitude,
    LocalDateTime timestamp,
    String notes,
    LocalDateTime createdAt
) {}

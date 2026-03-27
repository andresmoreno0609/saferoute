package com.saferoute.common.dto.stop;

import java.time.LocalDateTime;
import java.util.UUID;

public record StopResponse(
    UUID id,
    UUID routeId,
    String routeName,
    UUID studentId,
    String studentName,
    Integer orderNum,
    Double latitude,
    Double longitude,
    LocalDateTime arrivalTime,
    Boolean pickedUp,
    Boolean droppedOff,
    LocalDateTime createdAt
) {}

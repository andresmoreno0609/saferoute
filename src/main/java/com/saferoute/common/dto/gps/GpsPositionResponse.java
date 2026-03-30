package com.saferoute.common.dto.gps;

import java.time.LocalDateTime;
import java.util.UUID;

public record GpsPositionResponse(
    UUID id,
    UUID driverId,
    String driverName,
    UUID routeId,
    String routeName,
    Double latitude,
    Double longitude,
    LocalDateTime timestamp,
    Double speed,
    Double heading,
    Double accuracy,
    LocalDateTime createdAt
) {}

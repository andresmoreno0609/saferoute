package com.saferoute.common.dto.vehicle;

import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleResponse(
    UUID id,
    String plate,
    String model,
    String brand,
    String color,
    Integer capacity,
    UUID driverId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
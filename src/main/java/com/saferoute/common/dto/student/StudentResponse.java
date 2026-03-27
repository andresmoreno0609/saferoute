package com.saferoute.common.dto.student;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentResponse(
    UUID id,
    String name,
    String address,
    String schoolName,
    LocalDateTime createdAt
) {}

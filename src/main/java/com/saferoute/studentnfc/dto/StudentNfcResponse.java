package com.saferoute.studentnfc.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record StudentNfcResponse(
    UUID id,
    UUID studentId,
    String studentName,
    String nfcUid,
    Boolean isActive,
    LocalDateTime assignedAt,
    LocalDateTime deactivatedAt,
    UUID assignedBy,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
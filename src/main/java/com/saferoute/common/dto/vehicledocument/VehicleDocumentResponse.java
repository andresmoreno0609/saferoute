package com.saferoute.common.dto.vehicledocument;

import com.saferoute.common.entity.VehicleDocumentEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleDocumentResponse(
    UUID id,
    UUID vehicleId,
    VehicleDocumentEntity.VehicleDocumentType documentType,
    String fileUrl,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isActive,
    Boolean isVerified,
    LocalDateTime verifiedAt,
    UUID verifiedBy,
    String rejectionReason
) {}
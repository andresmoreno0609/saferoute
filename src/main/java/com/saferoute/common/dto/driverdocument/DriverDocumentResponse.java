package com.saferoute.common.dto.driverdocument;

import com.saferoute.common.entity.DriverDocumentEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DriverDocumentResponse(
    UUID id,
    UUID driverId,
    DriverDocumentEntity.DriverDocumentType documentType,
    String documentNumber,
    String licenseCategory,
    String fileUrl,
    LocalDate startDate,
    LocalDate endDate,
    Boolean isActive,
    Boolean isVerified,
    LocalDateTime verifiedAt,
    UUID verifiedBy,
    String rejectionReason
) {}
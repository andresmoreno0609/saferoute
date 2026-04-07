package com.saferoute.common.dto.driverdocument;

import com.saferoute.common.entity.DriverDocumentEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DriverDocumentRequest(
    @NotNull(message = "El tipo de documento es obligatorio")
    DriverDocumentEntity.DriverDocumentType documentType,

    String documentNumber,

    String licenseCategory,

    String fileUrl,

    LocalDate startDate,

    LocalDate endDate
) {}
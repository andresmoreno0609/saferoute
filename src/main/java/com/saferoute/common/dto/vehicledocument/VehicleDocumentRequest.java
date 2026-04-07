package com.saferoute.common.dto.vehicledocument;

import com.saferoute.common.entity.VehicleDocumentEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VehicleDocumentRequest(
    @NotNull(message = "El tipo de documento es obligatorio")
    VehicleDocumentEntity.VehicleDocumentType documentType,

    String fileUrl,

    LocalDate startDate,

    LocalDate endDate
) {}
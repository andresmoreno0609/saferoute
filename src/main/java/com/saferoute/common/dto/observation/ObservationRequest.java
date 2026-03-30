package com.saferoute.common.dto.observation;

import com.saferoute.common.entity.ObservationEntity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ObservationRequest(
    @NotNull(message = "El ID del estudiante es obligatorio")
    UUID studentId,

    @NotNull(message = "El ID del conductor es obligatorio")
    UUID driverId,

    @NotBlank(message = "La descripción es obligatoria")
    String description,

    String photoUrl,

    Severity severity
) {}

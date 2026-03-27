package com.saferoute.common.dto.route;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RouteRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotNull(message = "El ID del conductor es obligatorio")
    UUID driverId,

    @NotNull(message = "La fecha programada es obligatoria")
    LocalDate scheduledDate,

    String notes
) {}

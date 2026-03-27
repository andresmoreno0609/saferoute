package com.saferoute.common.dto.stop;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StopRequest(
    @NotNull(message = "El ID de la ruta es obligatorio")
    UUID routeId,

    @NotNull(message = "El ID del estudiante es obligatorio")
    UUID studentId,

    @NotNull(message = "El orden es obligatorio")
    @Positive(message = "El orden debe ser positivo")
    Integer orderNum,

    @NotNull(message = "La latitud es obligatoria")
    Double latitude,

    @NotNull(message = "La longitud es obligatoria")
    Double longitude
) {}

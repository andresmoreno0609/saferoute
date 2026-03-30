package com.saferoute.common.dto.gps;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GpsPositionRequest(
    @NotNull(message = "El ID del conductor es obligatorio")
    UUID driverId,

    @NotNull(message = "El ID de la ruta es obligatorio")
    UUID routeId,

    @NotNull(message = "La latitud es obligatoria")
    Double latitude,

    @NotNull(message = "La longitud es obligatoria")
    Double longitude,

    Double speed,
    Double heading,
    Double accuracy
) {}

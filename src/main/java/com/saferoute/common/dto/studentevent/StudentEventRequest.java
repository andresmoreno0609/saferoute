package com.saferoute.common.dto.studentevent;

import com.saferoute.common.entity.StudentEventEntity.EventType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StudentEventRequest(
    @NotNull(message = "El ID del estudiante es obligatorio")
    UUID studentId,

    @NotNull(message = "El ID del conductor es obligatorio")
    UUID driverId,

    @NotNull(message = "El ID de la ruta es obligatorio")
    UUID routeId,

    @NotNull(message = "El tipo de evento es obligatorio")
    EventType eventType,

    @NotNull(message = "La latitud es obligatoria")
    Double latitude,

    @NotNull(message = "La longitud es obligatoria")
    Double longitude,

    String notes
) {}

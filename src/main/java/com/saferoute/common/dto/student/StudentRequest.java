package com.saferoute.common.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    String address,

    @NotNull(message = "La latitud de casa es obligatoria")
    Double homeLatitude,

    @NotNull(message = "La longitud de casa es obligatoria")
    Double homeLongitude,

    @Size(max = 255, message = "El nombre del colegio no puede exceder 255 caracteres")
    String schoolName,

    Double schoolLatitude,
    Double schoolLongitude
) {}

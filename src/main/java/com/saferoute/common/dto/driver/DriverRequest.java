package com.saferoute.common.dto.driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DriverRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String phone,

    @NotBlank(message = "La placa del vehículo es obligatoria")
    @Size(max = 20, message = "La placa no puede exceder 20 caracteres")
    String vehiclePlate,

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    String vehicleModel,

    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    String vehicleColor,

    @NotNull(message = "El ID de usuario es obligatorio")
    UUID userId
) {}

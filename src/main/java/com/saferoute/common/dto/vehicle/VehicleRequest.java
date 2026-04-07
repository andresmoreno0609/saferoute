package com.saferoute.common.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VehicleRequest(
    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 20, message = "La placa no puede exceder 20 caracteres")
    String plate,

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    String model,

    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    String brand,

    @Size(max = 50, message = "El color no puede exceder 50 caracteres")
    String color,

    Integer capacity
) {}
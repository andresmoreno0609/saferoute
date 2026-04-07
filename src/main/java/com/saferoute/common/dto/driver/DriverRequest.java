package com.saferoute.common.dto.driver;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DriverRequest(
    @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @jakarta.validation.constraints.NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String phone,

    /**
     * ID del vehículo asociado al conductor.
     * El conductor debe tener un vehículo asignado para poder trabajar.
     */
    UUID vehicleId,

    /**
     * ID del usuario al que se le asignará el perfil de conductor.
     * Si no se proporciona, se usará el usuario autenticado (token JWT).
     * El ADMIN puede especificar cualquier userId para crear un conductor.
     */
    UUID userId
) {}

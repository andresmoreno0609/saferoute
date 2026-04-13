package com.saferoute.driver.usecase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request record for becoming a driver from existing user.
 */
public record BecomeDriverFromUserRequest(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID userId,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
        String name,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String phone,

        @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
        String documentNumber,

        LocalDate birthDate,

        @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
        String address,

        @Size(max = 50, message = "El número de licencia no puede exceder 50 caracteres")
        String licenseNumber,

        @Size(max = 10, message = "La categoría no puede exceder 10 caracteres")
        String licenseCategory,

        LocalDate licenseExpirationDate,

        @Size(max = 255, message = "El nombre de emergencia no puede exceder 255 caracteres")
        String emergencyContact,

        @Size(max = 20, message = "El teléfono de emergencia no puede exceder 20 caracteres")
        String emergencyPhone,

        Integer yearsExperience,

        @Size(max = 500, message = "La URL de foto no puede exceder 500 caracteres")
        String photoUrl,

        @Size(max = 50, message = "El banco no puede exceder 50 caracteres")
        String bankName,

        @Size(max = 50, message = "La cuenta no puede exceder 50 caracteres")
        String bankAccount,

        UUID vehicleId
) {}
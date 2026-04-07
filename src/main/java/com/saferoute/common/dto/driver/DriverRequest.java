package com.saferoute.common.dto.driver;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record DriverRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String phone,

    /**
     * Número de documento de identificación (cédula)
     */
    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    String documentNumber,

    /**
     * Fecha de nacimiento
     */
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    LocalDate birthDate,

    /**
     * Dirección de residencia
     */
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    String address,

    /**
     * Número de licencia de conducir
     */
    @Size(max = 50, message = "El número de licencia no puede exceder 50 caracteres")
    String licenseNumber,

    /**
     * Categoría de licencia (A, B, C, etc)
     */
    @Size(max = 10, message = "La categoría no puede exceder 10 caracteres")
    String licenseCategory,

    /**
     * Fecha de vencimiento de la licencia
     */
    LocalDate licenseExpirationDate,

    /**
     * Nombre del contacto de emergencia
     */
    @Size(max = 255, message = "El nombre de emergencia no puede exceder 255 caracteres")
    String emergencyContact,

    /**
     * Teléfono de contacto de emergencia
     */
    @Size(max = 20, message = "El teléfono de emergencia no puede exceder 20 caracteres")
    String emergencyPhone,

    /**
     * Años de experiencia conduciendo
     */
    Integer yearsExperience,

    /**
     * URL de la foto del conductor
     */
    @Size(max = 500, message = "La URL de foto no puede exceder 500 caracteres")
    String photoUrl,

    /**
     * Banco o método de pago (NEQUI, BANCOLOMBIA, DAVIPLATA, etc)
     */
    @Size(max = 50, message = "El banco no puede exceder 50 caracteres")
    String bankName,

    /**
     * Número de cuenta bancaria o móvil (opcional)
     */
    @Size(max = 50, message = "La cuenta no puede exceder 50 caracteres")
    String bankAccount,

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

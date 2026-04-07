package com.saferoute.common.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

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

    Double schoolLongitude,

    @Size(max = 50, message = "El grado no puede exceder 50 caracteres")
    String grade,

    LocalDate birthDate,

    @Size(max = 255, message = "El contacto de emergencia no puede exceder 255 caracteres")
    String emergencyContact,

    @Size(max = 20, message = "El teléfono de emergencia no puede exceder 20 caracteres")
    String emergencyPhone,

    @Size(max = 1000, message = "La info médica no puede exceder 1000 caracteres")
    String medicalInfo,

    @Size(max = 500, message = "La URL de foto no puede exceder 500 caracteres")
    String photoUrl,

    @Size(max = 50, message = "El código de estudiante no puede exceder 50 caracteres")
    String studentCode
) {}

package com.saferoute.common.dto.guardian;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GuardianRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String phone,

    @Email(message = "El email debe ser válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    String email,

    @Size(max = 500, message = "El token FCM no puede exceder 500 caracteres")
    String fcmToken,

    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    String documentNumber,

    LocalDate birthDate,

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    String address,

    @Size(max = 500, message = "La URL de foto no puede exceder 500 caracteres")
    String photoUrl,

    @Size(max = 255, message = "El contacto de emergencia no puede exceder 255 caracteres")
    String emergencyContact,

    @Size(max = 20, message = "El teléfono de emergencia no puede exceder 20 caracteres")
    String emergencyPhone,

    @Size(max = 100, message = "La ocupación no puede exceder 100 caracteres")
    String occupation,

    @Size(max = 20, message = "El teléfono del trabajo no puede exceder 20 caracteres")
    String workPhone
) {}

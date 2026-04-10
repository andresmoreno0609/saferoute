package com.saferoute.common.dto.user;

import com.saferoute.common.entity.UserEntity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserRequest(
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String name,

    @NotEmpty(message = "Debe especificar al menos un rol")
    Set<UserRole> roles
) {}

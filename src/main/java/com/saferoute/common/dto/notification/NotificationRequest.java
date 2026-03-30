package com.saferoute.common.dto.notification;

import com.saferoute.common.entity.NotificationEntity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationRequest(
    @NotNull(message = "El ID del acudiente es obligatorio")
    UUID guardianId,

    @NotBlank(message = "El título es obligatorio")
    String title,

    @NotBlank(message = "El cuerpo es obligatorio")
    String body,

    NotificationType type,

    UUID referenceId
) {}

package com.saferoute.common.dto.notification;

import com.saferoute.common.entity.NotificationEntity.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    UUID guardianId,
    String guardianName,
    String guardianEmail,
    String title,
    String body,
    NotificationType type,
    UUID referenceId,
    LocalDateTime sentAt,
    LocalDateTime readAt,
    boolean read
) {}

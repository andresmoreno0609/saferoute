package com.saferoute.notification.adapter;

import com.saferoute.common.dto.notification.NotificationRequest;
import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.common.service.NotificationService;
import com.saferoute.notification.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationAdapter {

    private final NotificationService service;
    private final CreateNotificationUseCase createUseCase;
    private final GetAllNotificationsUseCase getAllUseCase;
    private final GetNotificationByIdUseCase getByIdUseCase;
    private final GetNotificationsByGuardianUseCase getByGuardianUseCase;
    private final GetUnreadNotificationsUseCase getUnreadUseCase;
    private final MarkAsReadUseCase markAsReadUseCase;
    private final MarkAllAsReadUseCase markAllAsReadUseCase;

    public NotificationResponse create(NotificationRequest request) {
        return createUseCase.execute(request);
    }

    public List<NotificationResponse> getAll() {
        return getAllUseCase.execute(null);
    }

    public NotificationResponse getById(UUID id) {
        return getByIdUseCase.execute(id);
    }

    public List<NotificationResponse> getByGuardianId(UUID guardianId) {
        return getByGuardianUseCase.execute(guardianId);
    }

    public List<NotificationResponse> getUnread(UUID guardianId) {
        return getUnreadUseCase.execute(guardianId);
    }

    public NotificationResponse markAsRead(UUID id) {
        return markAsReadUseCase.execute(id);
    }

    public int markAllAsRead(UUID guardianId) {
        return markAllAsReadUseCase.execute(guardianId);
    }
}

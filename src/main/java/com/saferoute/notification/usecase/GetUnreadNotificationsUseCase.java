package com.saferoute.notification.usecase;

import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.common.service.NotificationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetUnreadNotificationsUseCase extends UseCaseAdvance<UUID, List<NotificationResponse>> {
    private final NotificationService service;

    @Override
    protected List<NotificationResponse> core(UUID guardianId) {
        log.debug("Getting unread notifications for guardian: {}", guardianId);
        return service.findUnreadByGuardianId(guardianId);
    }
}

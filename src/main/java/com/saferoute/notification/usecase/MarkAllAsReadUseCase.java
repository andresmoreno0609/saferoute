package com.saferoute.notification.usecase;

import com.saferoute.common.service.NotificationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkAllAsReadUseCase extends UseCaseAdvance<UUID, Integer> {
    private final NotificationService service;

    @Override
    protected Integer core(UUID guardianId) {
        log.debug("Marking all notifications as read for guardian: {}", guardianId);
        return service.markAllAsRead(guardianId);
    }
}

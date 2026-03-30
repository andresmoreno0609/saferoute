package com.saferoute.notification.usecase;

import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.common.service.NotificationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetNotificationByIdUseCase extends UseCaseAdvance<UUID, NotificationResponse> {
    private final NotificationService service;

    @Override
    protected NotificationResponse core(UUID id) {
        log.debug("Getting notification by id: {}", id);
        return service.findById(id);
    }
}

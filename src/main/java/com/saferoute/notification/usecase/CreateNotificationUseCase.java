package com.saferoute.notification.usecase;

import com.saferoute.common.dto.notification.NotificationRequest;
import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.common.service.NotificationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateNotificationUseCase extends UseCaseAdvance<NotificationRequest, NotificationResponse> {
    private final NotificationService service;

    @Override
    protected NotificationResponse core(NotificationRequest request) {
        log.debug("Creating notification for guardian: {}", request.guardianId());
        return service.create(request);
    }
}

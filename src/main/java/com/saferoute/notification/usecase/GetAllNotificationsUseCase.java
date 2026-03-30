package com.saferoute.notification.usecase;

import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.common.service.NotificationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllNotificationsUseCase extends UseCaseAdvance<Void, List<NotificationResponse>> {
    private final NotificationService service;

    @Override
    protected List<NotificationResponse> core(Void unused) {
        log.debug("Getting all notifications");
        return service.findAll();
    }
}

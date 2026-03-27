package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.service.GuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for updating FCM token for push notifications.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateFcmTokenUseCase extends UseCaseAdvance<UpdateFcmTokenRequest, GuardianResponse> {

    private final GuardianService guardianService;

    @Override
    protected GuardianResponse core(UpdateFcmTokenRequest request) {
        log.debug("Updating FCM token for guardian: {}", request.id());
        guardianService.updateFcmToken(request.id(), request.token());
        GuardianResponse guardian = guardianService.findById(request.id());
        log.info("FCM token updated for guardian: {}", request.id());
        return guardian;
    }
}

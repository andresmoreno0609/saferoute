package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.service.GuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for updating a guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateGuardianUseCase extends UseCaseAdvance<UpdateGuardianRequest, GuardianResponse> {

    private final GuardianService guardianService;

    @Override
    protected GuardianResponse core(UpdateGuardianRequest request) {
        log.debug("Updating guardian: {}", request.id());
        GuardianResponse updated = guardianService.update(request.id(), request.request());
        log.info("Guardian updated successfully: {}", request.id());
        return updated;
    }
}

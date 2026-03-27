package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.service.GuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for creating a new guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateGuardianUseCase extends UseCaseAdvance<GuardianRequest, GuardianResponse> {

    private final GuardianService guardianService;

    @Override
    protected GuardianResponse core(GuardianRequest request) {
        log.debug("Creating new guardian: {}", request.name());
        GuardianResponse created = guardianService.create(request);
        log.info("Guardian created successfully: {}", created.id());
        return created;
    }
}

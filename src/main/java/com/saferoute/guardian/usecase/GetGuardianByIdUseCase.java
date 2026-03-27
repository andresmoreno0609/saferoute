package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.common.service.GuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for getting a guardian by ID.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetGuardianByIdUseCase extends UseCaseAdvance<UUID, GuardianResponse> {

    private final GuardianService guardianService;

    @Override
    protected GuardianResponse core(UUID id) {
        log.debug("Fetching guardian by id: {}", id);
        return guardianService.findById(id);
    }
}

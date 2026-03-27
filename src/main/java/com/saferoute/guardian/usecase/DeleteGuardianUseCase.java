package com.saferoute.guardian.usecase;

import com.saferoute.common.service.GuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for deleting a guardian.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteGuardianUseCase extends UseCaseAdvance<UUID, Void> {

    private final GuardianService guardianService;

    @Override
    protected Void core(UUID id) {
        log.debug("Deleting guardian: {}", id);
        guardianService.delete(id);
        log.info("Guardian deleted successfully: {}", id);
        return null;
    }
}

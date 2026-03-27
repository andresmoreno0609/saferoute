package com.saferoute.driver.usecase;

import com.saferoute.common.service.DriverService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for deleting a driver.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteDriverUseCase extends UseCaseAdvance<UUID, Void> {

    private final DriverService driverService;

    @Override
    protected Void core(UUID id) {
        log.debug("Deleting driver: {}", id);
        driverService.delete(id);
        log.info("Driver deleted successfully: {}", id);
        return null;
    }
}

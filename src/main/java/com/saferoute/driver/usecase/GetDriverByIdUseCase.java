package com.saferoute.driver.usecase;

import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.service.DriverService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for getting a driver by ID.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetDriverByIdUseCase extends UseCaseAdvance<UUID, DriverResponse> {

    private final DriverService driverService;

    @Override
    protected DriverResponse core(UUID id) {
        log.debug("Fetching driver by id: {}", id);
        return driverService.findById(id);
    }
}

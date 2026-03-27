package com.saferoute.driver.usecase;

import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.service.DriverService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for updating a driver.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateDriverUseCase extends UseCaseAdvance<UpdateDriverRequest, DriverResponse> {

    private final DriverService driverService;

    @Override
    protected DriverResponse core(UpdateDriverRequest request) {
        log.debug("Updating driver: {}", request.id());
        DriverResponse updated = driverService.update(request.id(), request.request());
        log.info("Driver updated successfully: {}", request.id());
        return updated;
    }
}

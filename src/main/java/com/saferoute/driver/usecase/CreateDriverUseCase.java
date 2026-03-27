package com.saferoute.driver.usecase;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.service.DriverService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for creating a new driver.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateDriverUseCase extends UseCaseAdvance<DriverRequest, DriverResponse> {

    private final DriverService driverService;

    @Override
    protected DriverResponse core(DriverRequest request) {
        log.debug("Creating new driver: {}", request.name());
        DriverResponse created = driverService.create(request);
        log.info("Driver created successfully: {}", created.id());
        return created;
    }
}

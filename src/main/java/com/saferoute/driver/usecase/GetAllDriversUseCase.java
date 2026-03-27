package com.saferoute.driver.usecase;

import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.service.DriverService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use case for getting all drivers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllDriversUseCase extends UseCaseAdvance<Void, List<DriverResponse>> {

    private final DriverService driverService;

    @Override
    protected List<DriverResponse> core(Void request) {
        log.debug("Fetching all drivers");
        return driverService.findAll();
    }
}

package com.saferoute.driver.adapter;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.driver.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Driver Adapter - orchestrates use cases for driver management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DriverAdapter {

    private final GetAllDriversUseCase getAllDriversUseCase;
    private final GetDriverByIdUseCase getDriverByIdUseCase;
    private final CreateDriverUseCase createDriverUseCase;
    private final UpdateDriverUseCase updateDriverUseCase;
    private final DeleteDriverUseCase deleteDriverUseCase;

    /**
     * Get all drivers.
     */
    public List<DriverResponse> getAll() {
        return getAllDriversUseCase.execute(null);
    }

    /**
     * Get driver by ID.
     */
    public DriverResponse getById(UUID id) {
        return getDriverByIdUseCase.execute(id);
    }

    /**
     * Create a new driver.
     */
    public DriverResponse create(DriverRequest request) {
        return createDriverUseCase.execute(request);
    }

    /**
     * Update an existing driver.
     */
    public DriverResponse update(UUID id, DriverRequest request) {
        return updateDriverUseCase.execute(
            new UpdateDriverRequest(id, request)
        );
    }

    /**
     * Delete a driver.
     */
    public void delete(UUID id) {
        deleteDriverUseCase.execute(id);
    }
}

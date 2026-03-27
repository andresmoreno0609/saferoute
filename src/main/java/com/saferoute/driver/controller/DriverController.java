package com.saferoute.driver.controller;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.driver.adapter.DriverAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Driver REST Controller.
 * Handles CRUD operations for drivers.
 */
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverAdapter driverAdapter;

    /**
     * GET /api/v1/drivers
     * Get all drivers.
     */
    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAll() {
        log.info("GET /api/v1/drivers - Fetching all drivers");
        List<DriverResponse> drivers = driverAdapter.getAll();
        return ResponseEntity.ok(drivers);
    }

    /**
     * GET /api/v1/drivers/{id}
     * Get driver by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/drivers/{} - Fetching driver by id", id);
        DriverResponse driver = driverAdapter.getById(id);
        return ResponseEntity.ok(driver);
    }

    /**
     * POST /api/v1/drivers
     * Create a new driver.
     */
    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        log.info("POST /api/v1/drivers - Creating new driver");
        DriverResponse driver = driverAdapter.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    /**
     * PUT /api/v1/drivers/{id}
     * Update an existing driver.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DriverRequest request) {
        log.info("PUT /api/v1/drivers/{} - Updating driver", id);
        DriverResponse driver = driverAdapter.update(id, request);
        return ResponseEntity.ok(driver);
    }

    /**
     * DELETE /api/v1/drivers/{id}
     * Delete a driver.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/drivers/{} - Deleting driver", id);
        driverAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}

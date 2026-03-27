package com.saferoute.driver.usecase;

import com.saferoute.common.dto.driver.DriverRequest;

import java.util.UUID;

/**
 * Request record for updating a driver.
 */
public record UpdateDriverRequest(UUID id, DriverRequest request) {}

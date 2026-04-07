package com.saferoute.vehicle.adapter;

import com.saferoute.common.dto.vehicle.VehicleRequest;
import com.saferoute.common.dto.vehicle.VehicleResponse;
import com.saferoute.common.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Vehicle Adapter - orchestrates use cases for vehicle management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleAdapter {

    private final VehicleService vehicleService;

    public List<VehicleResponse> getAll() {
        return vehicleService.findAll();
    }

    public VehicleResponse getById(UUID id) {
        return vehicleService.findById(id);
    }

    public VehicleResponse create(VehicleRequest request) {
        return vehicleService.create(request);
    }

    public VehicleResponse update(UUID id, VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    public void delete(UUID id) {
        vehicleService.delete(id);
    }
}
package com.saferoute.common.service;

import com.saferoute.common.dto.vehicle.VehicleRequest;
import com.saferoute.common.dto.vehicle.VehicleResponse;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.entity.VehicleEntity;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Vehicle service implementing CRUD operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public VehicleResponse findById(UUID id) {
        VehicleEntity entity = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByPlate(request.plate())) {
            throw new VehicleAlreadyExistsException("Vehicle with plate already exists: " + request.plate());
        }

        VehicleEntity entity = VehicleEntity.builder()
                .plate(request.plate())
                .model(request.model())
                .brand(request.brand())
                .color(request.color())
                .capacity(request.capacity())
                .build();

        VehicleEntity saved = vehicleRepository.save(entity);
        log.info("Vehicle created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        VehicleEntity entity = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        // Check plate uniqueness if changing
        if (request.plate() != null && !request.plate().equals(entity.getPlate())) {
            if (vehicleRepository.existsByPlate(request.plate())) {
                throw new VehicleAlreadyExistsException("Vehicle with plate already exists: " + request.plate());
            }
            entity.setPlate(request.plate());
        }

        if (request.model() != null) {
            entity.setModel(request.model());
        }
        if (request.brand() != null) {
            entity.setBrand(request.brand());
        }
        if (request.color() != null) {
            entity.setColor(request.color());
        }
        if (request.capacity() != null) {
            entity.setCapacity(request.capacity());
        }

        VehicleEntity saved = vehicleRepository.save(entity);
        log.info("Vehicle updated: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        VehicleEntity entity = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        
        // Check if vehicle is assigned to a driver
        if (entity.getDriver() != null) {
            throw new VehicleAssignedException("Cannot delete vehicle assigned to a driver");
        }
        
        vehicleRepository.delete(entity);
        log.info("Vehicle deleted: {}", id);
    }

    public boolean existsById(UUID id) {
        return vehicleRepository.existsById(id);
    }

    @Transactional
    public VehicleResponse assignToDriver(UUID vehicleId, UUID driverId) {
        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + vehicleId));
        
        DriverEntity driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverService.DriverNotFoundException("Driver not found with id: " + driverId));
        
        // Check if driver already has a vehicle
        if (driver.getVehicle() != null) {
            throw new VehicleAssignedException("Driver already has a vehicle assigned");
        }
        
        // Check if vehicle is already assigned to another driver
        if (vehicle.getDriver() != null) {
            throw new VehicleAssignedException("Vehicle is already assigned to another driver");
        }
        
        vehicle.setDriver(driver);
        VehicleEntity saved = vehicleRepository.save(vehicle);
        log.info("Vehicle {} assigned to driver {}", vehicleId, driverId);
        return toResponse(saved);
    }

    private VehicleResponse toResponse(VehicleEntity entity) {
        return new VehicleResponse(
                entity.getId(),
                entity.getPlate(),
                entity.getModel(),
                entity.getBrand(),
                entity.getColor(),
                entity.getCapacity(),
                entity.getDriver() != null ? entity.getDriver().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Custom exceptions
    public static class VehicleNotFoundException extends RuntimeException {
        public VehicleNotFoundException(String message) {
            super(message);
        }
    }

    public static class VehicleAlreadyExistsException extends RuntimeException {
        public VehicleAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class VehicleAssignedException extends RuntimeException {
        public VehicleAssignedException(String message) {
            super(message);
        }
    }
}
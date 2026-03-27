package com.saferoute.common.service;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Driver service implementing CRUD operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public List<DriverResponse> findAll() {
        return driverRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public DriverResponse findById(UUID id) {
        DriverEntity entity = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        return toResponse(entity);
    }

    public DriverResponse create(DriverRequest request) {
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.userId()));

        DriverEntity entity = DriverEntity.builder()
                .user(user)
                .name(request.name())
                .phone(request.phone())
                .vehiclePlate(request.vehiclePlate())
                .vehicleModel(request.vehicleModel())
                .vehicleColor(request.vehicleColor())
                .build();

        DriverEntity saved = driverRepository.save(entity);
        log.info("Driver created: {}", saved.getId());
        return toResponse(saved);
    }

    public DriverResponse update(UUID id, DriverRequest request) {
        DriverEntity entity = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.phone() != null) {
            entity.setPhone(request.phone());
        }
        if (request.vehiclePlate() != null) {
            entity.setVehiclePlate(request.vehiclePlate());
        }
        if (request.vehicleModel() != null) {
            entity.setVehicleModel(request.vehicleModel());
        }
        if (request.vehicleColor() != null) {
            entity.setVehicleColor(request.vehicleColor());
        }

        DriverEntity saved = driverRepository.save(entity);
        log.info("Driver updated: {}", id);
        return toResponse(saved);
    }

    public void delete(UUID id) {
        DriverEntity entity = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        driverRepository.delete(entity);
        log.info("Driver deleted: {}", id);
    }

    public boolean existsById(UUID id) {
        return driverRepository.existsById(id);
    }

    private DriverResponse toResponse(DriverEntity entity) {
        return DriverResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .vehiclePlate(entity.getVehiclePlate())
                .vehicleModel(entity.getVehicleModel())
                .vehicleColor(entity.getVehicleColor())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Custom exceptions
    public static class DriverNotFoundException extends RuntimeException {
        public DriverNotFoundException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}

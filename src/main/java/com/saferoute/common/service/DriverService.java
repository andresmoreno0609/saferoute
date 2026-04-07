package com.saferoute.common.service;

import com.saferoute.common.dto.driver.DriverRequest;
import com.saferoute.common.dto.driver.DriverResponse;
import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.dto.vehicle.VehicleResponse;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.entity.DriverDocumentEntity;
import com.saferoute.common.entity.UserEntity;
import com.saferoute.common.entity.VehicleEntity;
import com.saferoute.common.repository.DriverDocumentRepository;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.UserRepository;
import com.saferoute.common.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final VehicleRepository vehicleRepository;
    private final DriverDocumentRepository driverDocumentRepository;

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

    @Transactional
    public DriverResponse create(DriverRequest request) {
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.userId()));

        DriverEntity entity = DriverEntity.builder()
                .user(user)
                .name(request.name())
                .phone(request.phone())
                .build();

        // If vehicleId is provided, assign vehicle
        if (request.vehicleId() != null) {
            VehicleEntity vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new VehicleService.VehicleNotFoundException("Vehicle not found with id: " + request.vehicleId()));
            
            if (vehicle.getDriver() != null) {
                throw new VehicleAssignedException("Vehicle is already assigned to another driver");
            }
            entity.setVehicle(vehicle);
        }

        DriverEntity saved = driverRepository.save(entity);
        log.info("Driver created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public DriverResponse update(UUID id, DriverRequest request) {
        DriverEntity entity = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.phone() != null) {
            entity.setPhone(request.phone());
        }
        if (request.vehicleId() != null) {
            VehicleEntity vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new VehicleService.VehicleNotFoundException("Vehicle not found with id: " + request.vehicleId()));
            
            if (vehicle.getDriver() != null && !vehicle.getDriver().getId().equals(id)) {
                throw new VehicleAssignedException("Vehicle is already assigned to another driver");
            }
            entity.setVehicle(vehicle);
        }

        DriverEntity saved = driverRepository.save(entity);
        log.info("Driver updated: {}", id);
        return toResponse(saved);
    }

    @Transactional
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
        // Get vehicle info
        VehicleResponse vehicleResponse = null;
        if (entity.getVehicle() != null) {
            VehicleEntity v = entity.getVehicle();
            vehicleResponse = new VehicleResponse(
                    v.getId(),
                    v.getPlate(),
                    v.getModel(),
                    v.getBrand(),
                    v.getColor(),
                    v.getCapacity(),
                    v.getDriver() != null ? v.getDriver().getId() : null,
                    v.getCreatedAt(),
                    v.getUpdatedAt()
            );
        }

        // Get documents
        List<DriverDocumentResponse> documents = driverDocumentRepository.findByDriverIdAndIsActiveTrue(entity.getId())
                .stream()
                .map(doc -> new DriverDocumentResponse(
                        doc.getId(),
                        doc.getDriver().getId(),
                        doc.getDocumentType(),
                        doc.getDocumentNumber(),
                        doc.getLicenseCategory(),
                        doc.getFileUrl(),
                        doc.getStartDate(),
                        doc.getEndDate(),
                        doc.getIsActive()
                ))
                .toList();

        return DriverResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .vehicleId(entity.getVehicle() != null ? entity.getVehicle().getId() : null)
                .vehicle(vehicleResponse)
                .documents(documents)
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

    public static class VehicleAssignedException extends RuntimeException {
        public VehicleAssignedException(String message) {
            super(message);
        }
    }
}

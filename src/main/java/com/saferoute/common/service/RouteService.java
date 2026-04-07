package com.saferoute.common.service;

import com.saferoute.common.dto.route.RouteRequest;
import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.entity.RouteEntity;
import com.saferoute.common.entity.RouteEntity.RouteStatus;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Route service with CRUD and status management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final DriverAvailabilityService driverAvailabilityService;

    public List<RouteResponse> findAll() {
        return routeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public RouteResponse findById(UUID id) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional
    public RouteResponse create(RouteRequest request) {
        DriverEntity driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + request.driverId()));

        // Validar disponibilidad del conductor antes de crear la ruta
        validateDriverAvailability(driver.getId());

        RouteEntity entity = RouteEntity.builder()
                .name(request.name())
                .driver(driver)
                .scheduledDate(request.scheduledDate())
                .notes(request.notes())
                .status(RouteStatus.PENDING)
                .build();

        RouteEntity saved = routeRepository.save(entity);
        log.info("Route created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public RouteResponse update(UUID id, RouteRequest request) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        // Validar disponibilidad si se está cambiando el conductor
        if (request.driverId() != null && !request.driverId().equals(entity.getDriver().getId())) {
            validateDriverAvailability(request.driverId());
        }

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.scheduledDate() != null) {
            entity.setScheduledDate(request.scheduledDate());
        }
        if (request.notes() != null) {
            entity.setNotes(request.notes());
        }
        if (request.driverId() != null) {
            DriverEntity newDriver = driverRepository.findById(request.driverId())
                    .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + request.driverId()));
            entity.setDriver(newDriver);
        }

        RouteEntity saved = routeRepository.save(entity);
        log.info("Route updated: {}", id);
        return toResponse(saved);
    }

    /**
     * Valida que el conductor pueda trabajar (documentos vigentes).
     * Lanza excepción si el conductor no está disponible.
     */
    private void validateDriverAvailability(UUID driverId) {
        DriverAvailabilityService.AvailabilityResult result = driverAvailabilityService.checkAvailability(driverId);
        
        if (!result.available()) {
            throw new DriverNotAvailableException(
                    "Conductor no disponible: " + result.reason() + 
                    ". Documentos faltantes: " + result.documentsMissing() +
                    ". Documentos vencidos: " + result.documentsExpired()
            );
        }
    }

    @Transactional
    public void delete(UUID id) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));
        routeRepository.delete(entity);
        log.info("Route deleted: {}", id);
    }

    @Transactional
    public RouteResponse startRoute(UUID id) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        if (entity.getStatus() != RouteStatus.PENDING) {
            throw new IllegalStateException("Only pending routes can be started");
        }

        // Validar disponibilidad antes de iniciar la ruta
        validateDriverAvailability(entity.getDriver().getId());

        entity.setStatus(RouteStatus.IN_PROGRESS);
        entity.setStartTime(LocalDateTime.now());

        RouteEntity saved = routeRepository.save(entity);
        log.info("Route started: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public RouteResponse completeRoute(UUID id) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        if (entity.getStatus() != RouteStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress routes can be completed");
        }

        entity.setStatus(RouteStatus.COMPLETED);
        entity.setEndTime(LocalDateTime.now());

        RouteEntity saved = routeRepository.save(entity);
        log.info("Route completed: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public RouteResponse cancelRoute(UUID id) {
        RouteEntity entity = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        if (entity.getStatus() == RouteStatus.COMPLETED) {
            throw new IllegalStateException("Completed routes cannot be cancelled");
        }

        entity.setStatus(RouteStatus.CANCELLED);
        if (entity.getStartTime() != null) {
            entity.setEndTime(LocalDateTime.now());
        }

        RouteEntity saved = routeRepository.save(entity);
        log.info("Route cancelled: {}", id);
        return toResponse(saved);
    }

    public boolean existsById(UUID id) {
        return routeRepository.existsById(id);
    }

    private RouteResponse toResponse(RouteEntity entity) {
        return new RouteResponse(
                entity.getId(),
                entity.getName(),
                entity.getDriver().getId(),
                entity.getDriver().getName(),
                entity.getStatus(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getScheduledDate(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static class RouteNotFoundException extends RuntimeException {
        public RouteNotFoundException(String message) {
            super(message);
        }
    }

    public static class DriverNotFoundException extends RuntimeException {
        public DriverNotFoundException(String message) {
            super(message);
        }
    }

    public static class DriverNotAvailableException extends RuntimeException {
        public DriverNotAvailableException(String message) {
            super(message);
        }
    }
}

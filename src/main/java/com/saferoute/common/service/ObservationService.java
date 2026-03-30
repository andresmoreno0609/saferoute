package com.saferoute.common.service;

import com.saferoute.common.dto.observation.ObservationRequest;
import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.common.entity.*;
import com.saferoute.common.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final StudentRepository studentRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;

    public ObservationResponse create(ObservationRequest request) {
        StudentEntity student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + request.studentId()));

        DriverEntity driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + request.driverId()));

        RouteEntity route = null;
        if (request.routeId() != null) {
            route = routeRepository.findById(request.routeId())
                    .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + request.routeId()));
        }

        ObservationEntity entity = ObservationEntity.builder()
                .student(student)
                .driver(driver)
                .route(route)
                .description(request.description())
                .photoUrl(request.photoUrl())
                .severity(request.severity() != null ? request.severity() : ObservationEntity.Severity.LOW)
                .timestamp(LocalDateTime.now())
                .build();

        ObservationEntity saved = observationRepository.save(entity);
        log.info("Observation created: {} - {}", saved.getId(), saved.getSeverity());
        return toResponse(saved);
    }

    public List<ObservationResponse> findAll() {
        return observationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ObservationResponse findById(UUID id) {
        return observationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ObservationNotFoundException("Observation not found with id: " + id));
    }

    public List<ObservationResponse> findByStudentId(UUID studentId) {
        return observationRepository.findByStudentIdOrderByTimestampDesc(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ObservationResponse> findByDriverId(UUID driverId) {
        return observationRepository.findByDriverIdOrderByTimestampDesc(driverId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ObservationResponse> findByRouteId(UUID routeId) {
        return observationRepository.findByRouteIdOrderByTimestampDesc(routeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ObservationResponse> findBySeverity(ObservationEntity.Severity severity) {
        return observationRepository.findBySeverity(severity).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ObservationResponse toResponse(ObservationEntity entity) {
        return new ObservationResponse(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getStudent().getName(),
                entity.getDriver().getId(),
                entity.getDriver().getName(),
                entity.getRoute() != null ? entity.getRoute().getId() : null,
                entity.getRoute() != null ? entity.getRoute().getName() : null,
                entity.getDescription(),
                entity.getPhotoUrl(),
                entity.getSeverity(),
                entity.getTimestamp(),
                entity.getCreatedAt()
        );
    }

    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) { super(message); }
    }

    public static class DriverNotFoundException extends RuntimeException {
        public DriverNotFoundException(String message) { super(message); }
    }

    public static class RouteNotFoundException extends RuntimeException {
        public RouteNotFoundException(String message) { super(message); }
    }

    public static class ObservationNotFoundException extends RuntimeException {
        public ObservationNotFoundException(String message) { super(message); }
    }
}

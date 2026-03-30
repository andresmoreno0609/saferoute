package com.saferoute.common.service;

import com.saferoute.common.dto.studentevent.StudentEventRequest;
import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.common.entity.*;
import com.saferoute.common.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentEventService {

    private final StudentEventRepository studentEventRepository;
    private final StudentRepository studentRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    public StudentEventResponse create(StudentEventRequest request) {
        StudentEntity student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));

        DriverEntity driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        RouteEntity route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        var location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));

        StudentEventEntity entity = StudentEventEntity.builder()
                .student(student)
                .driver(driver)
                .route(route)
                .eventType(request.eventType())
                .location(location)
                .timestamp(LocalDateTime.now())
                .notes(request.notes())
                .build();

        StudentEventEntity saved = studentEventRepository.save(entity);
        log.info("Student event created: {} - {}", saved.getId(), saved.getEventType());
        return toResponse(saved);
    }

    public List<StudentEventResponse> findByStudentId(UUID studentId) {
        return studentEventRepository.findByStudentIdOrderByTimestampDesc(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<StudentEventResponse> findByRouteId(UUID routeId) {
        return studentEventRepository.findByRouteIdOrderByTimestampDesc(routeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StudentEventResponse findLastEvent(UUID studentId, UUID routeId) {
        return studentEventRepository.findFirstByStudentIdAndRouteIdOrderByTimestampDesc(studentId, routeId)
                .map(this::toResponse)
                .orElse(null);
    }

    private StudentEventResponse toResponse(StudentEventEntity entity) {
        return new StudentEventResponse(
                entity.getId(),
                entity.getStudent().getId(),
                entity.getStudent().getName(),
                entity.getDriver().getId(),
                entity.getDriver().getName(),
                entity.getRoute().getId(),
                entity.getRoute().getName(),
                entity.getEventType(),
                entity.getLocation() != null ? entity.getLocation().getY() : null,
                entity.getLocation() != null ? entity.getLocation().getX() : null,
                entity.getTimestamp(),
                entity.getNotes(),
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
}

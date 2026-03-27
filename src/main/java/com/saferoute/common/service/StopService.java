package com.saferoute.common.service;

import com.saferoute.common.dto.stop.StopRequest;
import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.common.entity.RouteEntity;
import com.saferoute.common.entity.StudentEntity;
import com.saferoute.common.entity.StopEntity;
import com.saferoute.common.repository.RouteRepository;
import com.saferoute.common.repository.StopRepository;
import com.saferoute.common.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Stop service with CRUD and status management.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StopService {

    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final StudentRepository studentRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    public List<StopResponse> findAll() {
        return stopRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StopResponse findById(UUID id) {
        StopEntity entity = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));
        return toResponse(entity);
    }

    @Transactional
    public StopResponse create(StopRequest request) {
        RouteEntity route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + request.routeId()));

        StudentEntity student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + request.studentId()));

        // Check if order already exists for this route
        if (stopRepository.existsByRouteIdAndOrderNum(request.routeId(), request.orderNum())) {
            throw new IllegalArgumentException("Order already exists for this route");
        }

        var location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));

        StopEntity entity = StopEntity.builder()
                .route(route)
                .student(student)
                .orderNum(request.orderNum())
                .location(location)
                .pickedUp(false)
                .droppedOff(false)
                .build();

        StopEntity saved = stopRepository.save(entity);
        log.info("Stop created: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public StopResponse update(UUID id, StopRequest request) {
        StopEntity entity = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));

        if (request.orderNum() != null) {
            entity.setOrderNum(request.orderNum());
        }
        if (request.latitude() != null && request.longitude() != null) {
            entity.setLocation(geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude())));
        }

        StopEntity saved = stopRepository.save(entity);
        log.info("Stop updated: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        StopEntity entity = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));
        stopRepository.delete(entity);
        log.info("Stop deleted: {}", id);
    }

    @Transactional
    public StopResponse markPickedUp(UUID id) {
        StopEntity entity = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));

        entity.setPickedUp(true);
        entity.setArrivalTime(LocalDateTime.now());

        StopEntity saved = stopRepository.save(entity);
        log.info("Stop picked up: {}", id);
        return toResponse(saved);
    }

    @Transactional
    public StopResponse markDroppedOff(UUID id) {
        StopEntity entity = stopRepository.findById(id)
                .orElseThrow(() -> new StopNotFoundException("Stop not found with id: " + id));

        entity.setDroppedOff(true);

        StopEntity saved = stopRepository.save(entity);
        log.info("Stop dropped off: {}", id);
        return toResponse(saved);
    }

    public List<StopResponse> findByRouteId(UUID routeId) {
        return stopRepository.findByRouteIdOrderByOrderNumAsc(routeId).stream()
                .map(this::toResponse)
                .toList();
    }

    private StopResponse toResponse(StopEntity entity) {
        return new StopResponse(
                entity.getId(),
                entity.getRoute().getId(),
                entity.getRoute().getName(),
                entity.getStudent().getId(),
                entity.getStudent().getName(),
                entity.getOrderNum(),
                entity.getLocation() != null ? entity.getLocation().getY() : null,
                entity.getLocation() != null ? entity.getLocation().getX() : null,
                entity.getArrivalTime(),
                entity.getPickedUp(),
                entity.getDroppedOff(),
                entity.getCreatedAt()
        );
    }

    public static class StopNotFoundException extends RuntimeException {
        public StopNotFoundException(String message) { super(message); }
    }

    public static class RouteNotFoundException extends RuntimeException {
        public RouteNotFoundException(String message) { super(message); }
    }

    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) { super(message); }
    }
}

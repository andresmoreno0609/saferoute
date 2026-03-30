package com.saferoute.common.service;

import com.saferoute.common.dto.gps.GpsPositionRequest;
import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.common.entity.DriverEntity;
import com.saferoute.common.entity.GpsPositionEntity;
import com.saferoute.common.entity.RouteEntity;
import com.saferoute.common.repository.DriverRepository;
import com.saferoute.common.repository.GpsPositionRepository;
import com.saferoute.common.repository.RouteRepository;
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
public class GpsPositionService {

    private final GpsPositionRepository gpsPositionRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);

    public GpsPositionResponse create(GpsPositionRequest request) {
        DriverEntity driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        RouteEntity route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        var location = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));

        GpsPositionEntity entity = GpsPositionEntity.builder()
                .driver(driver)
                .route(route)
                .location(location)
                .timestamp(LocalDateTime.now())
                .speed(request.speed())
                .heading(request.heading())
                .accuracy(request.accuracy())
                .build();

        GpsPositionEntity saved = gpsPositionRepository.save(entity);
        log.info("GPS position saved for route: {}", saved.getRoute().getId());
        return toResponse(saved);
    }

    public List<GpsPositionResponse> findByRouteId(UUID routeId) {
        return gpsPositionRepository.findByRouteIdOrderByTimestampDesc(routeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public GpsPositionResponse findCurrentPosition(UUID routeId) {
        return gpsPositionRepository.findFirstByRouteIdOrderByTimestampDesc(routeId)
                .map(this::toResponse)
                .orElse(null);
    }

    public List<GpsPositionResponse> findByDriverId(UUID driverId) {
        return gpsPositionRepository.findByDriverIdOrderByTimestampDesc(driverId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private GpsPositionResponse toResponse(GpsPositionEntity entity) {
        return new GpsPositionResponse(
                entity.getId(),
                entity.getDriver().getId(),
                entity.getDriver().getName(),
                entity.getRoute().getId(),
                entity.getRoute().getName(),
                entity.getLocation() != null ? entity.getLocation().getY() : null,
                entity.getLocation() != null ? entity.getLocation().getX() : null,
                entity.getTimestamp(),
                entity.getSpeed(),
                entity.getHeading(),
                entity.getAccuracy(),
                entity.getCreatedAt()
        );
    }

    public static class DriverNotFoundException extends RuntimeException {
        public DriverNotFoundException(String message) { super(message); }
    }

    public static class RouteNotFoundException extends RuntimeException {
        public RouteNotFoundException(String message) { super(message); }
    }
}

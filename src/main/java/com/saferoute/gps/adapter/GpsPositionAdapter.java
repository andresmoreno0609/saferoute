package com.saferoute.gps.adapter;

import com.saferoute.common.dto.gps.GpsPositionRequest;
import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.gps.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GpsPositionAdapter {

    private final CreateGpsPositionUseCase createUseCase;
    private final GetGpsPositionsByRouteUseCase getByRouteUseCase;
    private final GetCurrentPositionUseCase getCurrentUseCase;
    private final GetGpsPositionsByDriverUseCase getByDriverUseCase;

    public GpsPositionResponse create(GpsPositionRequest request) {
        return createUseCase.execute(request);
    }

    public List<GpsPositionResponse> getByRouteId(UUID routeId) {
        return getByRouteUseCase.execute(routeId);
    }

    public GpsPositionResponse getCurrentPosition(UUID routeId) {
        return getCurrentUseCase.execute(routeId);
    }

    public List<GpsPositionResponse> getByDriverId(UUID driverId) {
        return getByDriverUseCase.execute(driverId);
    }
}

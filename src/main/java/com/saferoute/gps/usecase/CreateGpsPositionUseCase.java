package com.saferoute.gps.usecase;

import com.saferoute.common.dto.gps.GpsPositionRequest;
import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.common.service.GpsPositionService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateGpsPositionUseCase extends UseCaseAdvance<GpsPositionRequest, GpsPositionResponse> {
    private final GpsPositionService service;

    @Override
    protected GpsPositionResponse core(GpsPositionRequest request) {
        log.debug("Creating GPS position for route: {}", request.routeId());
        return service.create(request);
    }
}

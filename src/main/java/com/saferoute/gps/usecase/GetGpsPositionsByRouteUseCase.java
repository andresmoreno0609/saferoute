package com.saferoute.gps.usecase;

import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.common.service.GpsPositionService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetGpsPositionsByRouteUseCase extends UseCaseAdvance<UUID, List<GpsPositionResponse>> {
    private final GpsPositionService service;

    @Override
    protected List<GpsPositionResponse> core(UUID routeId) {
        return service.findByRouteId(routeId);
    }
}

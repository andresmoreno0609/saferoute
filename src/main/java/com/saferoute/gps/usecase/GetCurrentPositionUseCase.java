package com.saferoute.gps.usecase;

import com.saferoute.common.dto.gps.GpsPositionResponse;
import com.saferoute.common.service.GpsPositionService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetCurrentPositionUseCase extends UseCaseAdvance<UUID, GpsPositionResponse> {
    private final GpsPositionService service;

    @Override
    protected GpsPositionResponse core(UUID routeId) {
        return service.findCurrentPosition(routeId);
    }
}

package com.saferoute.route.usecase;

import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.common.service.RouteService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartRouteUseCase extends UseCaseAdvance<UUID, RouteResponse> {
    private final RouteService service;

    @Override
    protected RouteResponse core(UUID id) {
        log.debug("Starting route: {}", id);
        return service.startRoute(id);
    }
}

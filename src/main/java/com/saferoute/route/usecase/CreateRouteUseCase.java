package com.saferoute.route.usecase;

import com.saferoute.common.dto.route.RouteRequest;
import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.common.service.RouteService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateRouteUseCase extends UseCaseAdvance<RouteRequest, RouteResponse> {
    private final RouteService service;

    @Override
    protected RouteResponse core(RouteRequest request) {
        log.debug("Creating new route: {}", request.name());
        return service.create(request);
    }
}

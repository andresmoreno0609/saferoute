package com.saferoute.route.usecase;

import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.common.service.RouteService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateRouteUseCase extends UseCaseAdvance<UpdateRouteRequest, RouteResponse> {
    private final RouteService service;

    @Override
    protected RouteResponse core(UpdateRouteRequest request) {
        log.debug("Updating route: {}", request.id());
        return service.update(request.id(), request.request());
    }
}

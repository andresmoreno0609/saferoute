package com.saferoute.route.usecase;

import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.common.service.RouteService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllRoutesUseCase extends UseCaseAdvance<Void, List<RouteResponse>> {
    private final RouteService service;

    @Override
    protected List<RouteResponse> core(Void request) {
        return service.findAll();
    }
}

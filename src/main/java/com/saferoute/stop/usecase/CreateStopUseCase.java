package com.saferoute.stop.usecase;

import com.saferoute.common.dto.stop.StopRequest;
import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.common.service.StopService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateStopUseCase extends UseCaseAdvance<StopRequest, StopResponse> {
    private final StopService service;

    @Override
    protected StopResponse core(StopRequest request) {
        log.debug("Creating new stop for route: {}", request.routeId());
        return service.create(request);
    }
}

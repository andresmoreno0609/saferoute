package com.saferoute.stop.usecase;

import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.common.service.StopService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetStopsByRouteUseCase extends UseCaseAdvance<UUID, List<StopResponse>> {
    private final StopService service;

    @Override
    protected List<StopResponse> core(UUID routeId) {
        return service.findByRouteId(routeId);
    }
}

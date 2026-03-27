package com.saferoute.route.usecase;

import com.saferoute.common.service.RouteService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteRouteUseCase extends UseCaseAdvance<UUID, Void> {
    private final RouteService service;

    @Override
    protected Void core(UUID id) {
        log.debug("Deleting route: {}", id);
        service.delete(id);
        return null;
    }
}

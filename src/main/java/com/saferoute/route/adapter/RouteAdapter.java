package com.saferoute.route.adapter;

import com.saferoute.common.dto.route.RouteRequest;
import com.saferoute.common.dto.route.RouteResponse;
import com.saferoute.route.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteAdapter {

    private final GetAllRoutesUseCase getAllUseCase;
    private final GetRouteByIdUseCase getByIdUseCase;
    private final CreateRouteUseCase createUseCase;
    private final UpdateRouteUseCase updateUseCase;
    private final DeleteRouteUseCase deleteUseCase;
    private final StartRouteUseCase startRouteUseCase;
    private final CompleteRouteUseCase completeRouteUseCase;
    private final CancelRouteUseCase cancelRouteUseCase;

    public List<RouteResponse> getAll() {
        return getAllUseCase.execute(null);
    }

    public RouteResponse getById(UUID id) {
        return getByIdUseCase.execute(id);
    }

    public RouteResponse create(RouteRequest request) {
        return createUseCase.execute(request);
    }

    public RouteResponse update(UUID id, RouteRequest request) {
        return updateUseCase.execute(
            new com.saferoute.route.usecase.UpdateRouteRequest(id, request)
        );
    }

    public void delete(UUID id) {
        deleteUseCase.execute(id);
    }

    public RouteResponse startRoute(UUID id) {
        return startRouteUseCase.execute(id);
    }

    public RouteResponse completeRoute(UUID id) {
        return completeRouteUseCase.execute(id);
    }

    public RouteResponse cancelRoute(UUID id) {
        return cancelRouteUseCase.execute(id);
    }
}

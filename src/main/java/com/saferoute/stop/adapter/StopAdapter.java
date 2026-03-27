package com.saferoute.stop.adapter;

import com.saferoute.common.dto.stop.StopRequest;
import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.stop.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StopAdapter {

    private final GetAllStopsUseCase getAllUseCase;
    private final GetStopByIdUseCase getByIdUseCase;
    private final CreateStopUseCase createUseCase;
    private final UpdateStopUseCase updateUseCase;
    private final DeleteStopUseCase deleteUseCase;
    private final MarkPickedUpUseCase markPickedUpUseCase;
    private final MarkDroppedOffUseCase markDroppedOffUseCase;
    private final GetStopsByRouteUseCase getByRouteUseCase;

    public List<StopResponse> getAll() {
        return getAllUseCase.execute(null);
    }

    public StopResponse getById(UUID id) {
        return getByIdUseCase.execute(id);
    }

    public StopResponse create(StopRequest request) {
        return createUseCase.execute(request);
    }

    public StopResponse update(UUID id, StopRequest request) {
        return updateUseCase.execute(
            new com.saferoute.stop.usecase.UpdateStopRequest(id, request)
        );
    }

    public void delete(UUID id) {
        deleteUseCase.execute(id);
    }

    public StopResponse markPickedUp(UUID id) {
        return markPickedUpUseCase.execute(id);
    }

    public StopResponse markDroppedOff(UUID id) {
        return markDroppedOffUseCase.execute(id);
    }

    public List<StopResponse> getByRouteId(UUID routeId) {
        return getByRouteUseCase.execute(routeId);
    }
}

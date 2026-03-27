package com.saferoute.stop.usecase;

import com.saferoute.common.dto.stop.StopResponse;
import com.saferoute.common.service.StopService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateStopUseCase extends UseCaseAdvance<UpdateStopRequest, StopResponse> {
    private final StopService service;

    @Override
    protected StopResponse core(UpdateStopRequest request) {
        log.debug("Updating stop: {}", request.id());
        return service.update(request.id(), request.request());
    }
}

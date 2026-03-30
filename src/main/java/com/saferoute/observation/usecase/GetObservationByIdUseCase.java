package com.saferoute.observation.usecase;

import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.common.service.ObservationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetObservationByIdUseCase extends UseCaseAdvance<UUID, ObservationResponse> {
    private final ObservationService service;

    @Override
    protected ObservationResponse core(UUID id) {
        log.debug("Getting observation by id: {}", id);
        return service.findById(id);
    }
}

package com.saferoute.observation.usecase;

import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.common.service.ObservationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllObservationsUseCase extends UseCaseAdvance<Void, List<ObservationResponse>> {
    private final ObservationService service;

    @Override
    protected List<ObservationResponse> core(Void unused) {
        log.debug("Getting all observations");
        return service.findAll();
    }
}

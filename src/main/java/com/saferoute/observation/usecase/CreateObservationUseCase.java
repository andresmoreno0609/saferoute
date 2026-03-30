package com.saferoute.observation.usecase;

import com.saferoute.common.dto.observation.ObservationRequest;
import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.common.service.ObservationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateObservationUseCase extends UseCaseAdvance<ObservationRequest, ObservationResponse> {
    private final ObservationService service;

    @Override
    protected ObservationResponse core(ObservationRequest request) {
        log.debug("Creating observation for student: {}", request.studentId());
        return service.create(request);
    }
}

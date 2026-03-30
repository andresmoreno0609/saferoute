package com.saferoute.observation.usecase;

import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.common.service.ObservationService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetObservationsByStudentUseCase extends UseCaseAdvance<UUID, List<ObservationResponse>> {
    private final ObservationService service;

    @Override
    protected List<ObservationResponse> core(UUID studentId) {
        log.debug("Getting observations for student: {}", studentId);
        return service.findByStudentId(studentId);
    }
}

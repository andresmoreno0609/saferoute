package com.saferoute.observation.adapter;

import com.saferoute.common.dto.observation.ObservationRequest;
import com.saferoute.common.dto.observation.ObservationResponse;
import com.saferoute.common.service.ObservationService;
import com.saferoute.observation.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservationAdapter {

    private final ObservationService service;
    private final CreateObservationUseCase createUseCase;
    private final GetAllObservationsUseCase getAllUseCase;
    private final GetObservationByIdUseCase getByIdUseCase;
    private final GetObservationsByStudentUseCase getByStudentUseCase;

    public ObservationResponse create(ObservationRequest request) {
        return createUseCase.execute(request);
    }

    public List<ObservationResponse> getAll() {
        return getAllUseCase.execute(null);
    }

    public ObservationResponse getById(UUID id) {
        return getByIdUseCase.execute(id);
    }

    public List<ObservationResponse> getByStudentId(UUID studentId) {
        return getByStudentUseCase.execute(studentId);
    }
}

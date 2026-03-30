package com.saferoute.studentevent.usecase;

import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.common.service.StudentEventService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetStudentEventsByRouteUseCase extends UseCaseAdvance<UUID, List<StudentEventResponse>> {
    private final StudentEventService service;

    @Override
    protected List<StudentEventResponse> core(UUID routeId) {
        return service.findByRouteId(routeId);
    }
}

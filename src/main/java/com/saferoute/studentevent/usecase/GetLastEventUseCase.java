package com.saferoute.studentevent.usecase;

import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.common.service.StudentEventService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetLastEventUseCase extends UseCaseAdvance<StudentLastEventRequest, StudentEventResponse> {
    private final StudentEventService service;

    @Override
    protected StudentEventResponse core(StudentLastEventRequest request) {
        return service.findLastEvent(request.studentId(), request.routeId());
    }
}

package com.saferoute.studentevent.usecase;

import com.saferoute.common.dto.studentevent.StudentEventRequest;
import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.common.service.StudentEventService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateStudentEventUseCase extends UseCaseAdvance<StudentEventRequest, StudentEventResponse> {
    private final StudentEventService service;

    @Override
    protected StudentEventResponse core(StudentEventRequest request) {
        log.debug("Creating student event: {}", request.eventType());
        return service.create(request);
    }
}

package com.saferoute.studentevent.adapter;

import com.saferoute.common.dto.studentevent.StudentEventRequest;
import com.saferoute.common.dto.studentevent.StudentEventResponse;
import com.saferoute.studentevent.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentEventAdapter {

    private final CreateStudentEventUseCase createUseCase;
    private final GetStudentEventsByStudentUseCase getByStudentUseCase;
    private final GetStudentEventsByRouteUseCase getByRouteUseCase;
    private final GetLastEventUseCase getLastEventUseCase;

    public StudentEventResponse create(StudentEventRequest request) {
        return createUseCase.execute(request);
    }

    public List<StudentEventResponse> getByStudentId(UUID studentId) {
        return getByStudentUseCase.execute(studentId);
    }

    public List<StudentEventResponse> getByRouteId(UUID routeId) {
        return getByRouteUseCase.execute(routeId);
    }

    public StudentEventResponse getLastEvent(UUID studentId, UUID routeId) {
        return getLastEventUseCase.execute(
            new com.saferoute.studentevent.usecase.StudentLastEventRequest(studentId, routeId)
        );
    }
}

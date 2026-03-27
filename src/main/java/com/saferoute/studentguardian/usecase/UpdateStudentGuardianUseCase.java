package com.saferoute.studentguardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateStudentGuardianUseCase extends UseCaseAdvance<UpdateStudentGuardianRequest, StudentGuardianResponse> {
    private final StudentGuardianService service;

    @Override
    protected StudentGuardianResponse core(UpdateStudentGuardianRequest request) {
        log.debug("Updating student-guardian relationship: {}", request.id());
        return service.update(request.id(), request.request());
    }
}

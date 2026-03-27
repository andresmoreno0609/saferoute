package com.saferoute.studentguardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateStudentGuardianUseCase extends UseCaseAdvance<StudentGuardianRequest, StudentGuardianResponse> {
    private final StudentGuardianService service;

    @Override
    protected StudentGuardianResponse core(StudentGuardianRequest request) {
        log.debug("Creating student-guardian relationship");
        return service.create(request);
    }
}

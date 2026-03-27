package com.saferoute.studentguardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetStudentGuardianByIdUseCase extends UseCaseAdvance<UUID, StudentGuardianResponse> {
    private final StudentGuardianService service;

    @Override
    protected StudentGuardianResponse core(UUID id) {
        return service.findById(id);
    }
}

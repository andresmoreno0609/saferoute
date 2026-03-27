package com.saferoute.studentguardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllStudentGuardiansUseCase extends UseCaseAdvance<Void, List<StudentGuardianResponse>> {
    private final StudentGuardianService service;

    @Override
    protected List<StudentGuardianResponse> core(Void request) {
        return service.findAll();
    }
}

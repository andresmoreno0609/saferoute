package com.saferoute.studentguardian.usecase;

import com.saferoute.common.service.StudentGuardianService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteStudentGuardianUseCase extends UseCaseAdvance<UUID, Void> {
    private final StudentGuardianService service;

    @Override
    protected Void core(UUID id) {
        log.debug("Deleting student-guardian relationship: {}", id);
        service.delete(id);
        return null;
    }
}

package com.saferoute.student.usecase;

import com.saferoute.common.service.StudentService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for deleting a student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteStudentUseCase extends UseCaseAdvance<UUID, Void> {

    private final StudentService studentService;

    @Override
    protected Void core(UUID id) {
        log.debug("Deleting student: {}", id);
        studentService.delete(id);
        log.info("Student deleted successfully: {}", id);
        return null;
    }
}

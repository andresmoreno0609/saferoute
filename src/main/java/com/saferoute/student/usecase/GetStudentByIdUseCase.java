package com.saferoute.student.usecase;

import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.service.StudentService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for getting a student by ID.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetStudentByIdUseCase extends UseCaseAdvance<UUID, StudentResponse> {

    private final StudentService studentService;

    @Override
    protected StudentResponse core(UUID id) {
        log.debug("Fetching student by id: {}", id);
        return studentService.findById(id);
    }
}

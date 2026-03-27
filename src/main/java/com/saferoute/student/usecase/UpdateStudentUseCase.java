package com.saferoute.student.usecase;

import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.service.StudentService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for updating a student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateStudentUseCase extends UseCaseAdvance<UpdateStudentRequest, StudentResponse> {

    private final StudentService studentService;

    @Override
    protected StudentResponse core(UpdateStudentRequest request) {
        log.debug("Updating student: {}", request.id());
        StudentResponse updated = studentService.update(request.id(), request.request());
        log.info("Student updated successfully: {}", request.id());
        return updated;
    }
}

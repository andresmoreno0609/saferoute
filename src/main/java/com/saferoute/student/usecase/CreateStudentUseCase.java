package com.saferoute.student.usecase;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.service.StudentService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Use case for creating a new student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateStudentUseCase extends UseCaseAdvance<StudentRequest, StudentResponse> {

    private final StudentService studentService;

    @Override
    protected StudentResponse core(StudentRequest request) {
        log.debug("Creating new student: {}", request.name());
        StudentResponse created = studentService.create(request);
        log.info("Student created successfully: {}", created.id());
        return created;
    }
}

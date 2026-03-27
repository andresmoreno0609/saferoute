package com.saferoute.student.usecase;

import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.common.service.StudentService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use case for getting all students.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllStudentsUseCase extends UseCaseAdvance<Void, List<StudentResponse>> {

    private final StudentService studentService;

    @Override
    protected List<StudentResponse> core(Void request) {
        log.debug("Fetching all students");
        return studentService.findAll();
    }
}

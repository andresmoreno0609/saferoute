package com.saferoute.student.adapter;

import com.saferoute.common.dto.student.StudentRequest;
import com.saferoute.common.dto.student.StudentResponse;
import com.saferoute.student.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Student Adapter - orchestrates use cases for student management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentAdapter {

    private final GetAllStudentsUseCase getAllStudentsUseCase;
    private final GetStudentByIdUseCase getStudentByIdUseCase;
    private final CreateStudentUseCase createStudentUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;
    private final DeleteStudentUseCase deleteStudentUseCase;

    /**
     * Get all students.
     */
    public List<StudentResponse> getAll() {
        return getAllStudentsUseCase.execute(null);
    }

    /**
     * Get student by ID.
     */
    public StudentResponse getById(UUID id) {
        return getStudentByIdUseCase.execute(id);
    }

    /**
     * Create a new student.
     */
    public StudentResponse create(StudentRequest request) {
        return createStudentUseCase.execute(request);
    }

    /**
     * Update an existing student.
     */
    public StudentResponse update(UUID id, StudentRequest request) {
        return updateStudentUseCase.execute(
            new com.saferoute.student.usecase.UpdateStudentRequest(id, request)
        );
    }

    /**
     * Delete a student.
     */
    public void delete(UUID id) {
        deleteStudentUseCase.execute(id);
    }
}

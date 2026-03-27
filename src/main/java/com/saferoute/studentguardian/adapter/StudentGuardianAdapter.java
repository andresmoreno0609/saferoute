package com.saferoute.studentguardian.adapter;

import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;
import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.studentguardian.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Student-Guardian Adapter.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentGuardianAdapter {

    private final GetAllStudentGuardiansUseCase getAllUseCase;
    private final GetStudentGuardianByIdUseCase getByIdUseCase;
    private final CreateStudentGuardianUseCase createUseCase;
    private final UpdateStudentGuardianUseCase updateUseCase;
    private final DeleteStudentGuardianUseCase deleteUseCase;
    private final GetGuardiansByStudentUseCase getByStudentUseCase;
    private final GetStudentsByGuardianUseCase getByGuardianUseCase;

    public List<StudentGuardianResponse> getAll() {
        return getAllUseCase.execute(null);
    }

    public StudentGuardianResponse getById(UUID id) {
        return getByIdUseCase.execute(id);
    }

    public StudentGuardianResponse create(StudentGuardianRequest request) {
        return createUseCase.execute(request);
    }

    public StudentGuardianResponse update(UUID id, StudentGuardianRequest request) {
        return updateUseCase.execute(new UpdateStudentGuardianRequest(id, request));
    }

    public void delete(UUID id) {
        deleteUseCase.execute(id);
    }

    public List<StudentGuardianResponse> getByStudentId(UUID studentId) {
        return getByStudentUseCase.execute(studentId);
    }

    public List<StudentGuardianResponse> getByGuardianId(UUID guardianId) {
        return getByGuardianUseCase.execute(guardianId);
    }
}

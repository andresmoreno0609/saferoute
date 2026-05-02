package com.saferoute.guardian.service;

import com.saferoute.common.dto.studentguardian.StudentGuardianResponse;
import com.saferoute.guardian.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servicio que delega a los UseCases la lógica de negocio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuardianStudentService {

    private final CreateGuardianStudentUseCase createUseCase;
    private final UpdateGuardianStudentUseCase updateUseCase;
    private final DeleteGuardianStudentUseCase deleteUseCase;
    private final GetGuardianStudentsUseCase getAllUseCase;

    public StudentGuardianResponse createStudentForGuardian(UUID guardianId, GuardianStudentData data) {
        return createUseCase.execute(new CreateGuardianStudentRequest(guardianId, data));
    }

    public StudentGuardianResponse updateStudentForGuardian(UUID guardianId, UUID studentId, GuardianStudentData data) {
        return updateUseCase.execute(new UpdateGuardianStudentRequest(guardianId, studentId, data));
    }

    public void deleteStudentForGuardian(UUID guardianId, UUID studentId) {
        deleteUseCase.execute(new DeleteGuardianStudentRequest(guardianId, studentId));
    }

    public List<StudentGuardianResponse> getStudentsByGuardian(UUID guardianId) {
        return getAllUseCase.execute(guardianId);
    }
}
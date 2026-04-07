package com.saferoute.studentnfc.usecase;

import com.saferoute.common.service.StudentNfcService;
import com.saferoute.common.usecase.UseCaseAdvance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case for deactivating an NFC tag from a student.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeactivateNfcUseCase extends UseCaseAdvance<UUID, Void> {

    private final StudentNfcService studentNfcService;

    @Override
    protected void preConditions(UUID studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
    }

    @Override
    protected Void core(UUID studentId) {
        log.info("Deactivating NFC for student {}", studentId);
        studentNfcService.deactivateNfc(studentId);
        return null;
    }
}
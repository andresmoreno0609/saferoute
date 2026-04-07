package com.saferoute.studentnfc.adapter;

import com.saferoute.studentnfc.dto.AssignNfcRequest;
import com.saferoute.studentnfc.dto.NfcScanRequest;
import com.saferoute.studentnfc.dto.StudentNfcResponse;
import com.saferoute.studentnfc.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * StudentNfc Adapter - orchestrates use cases for NFC management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentNfcAdapter {

    private final AssignNfcUseCase assignNfcUseCase;
    private final DeactivateNfcUseCase deactivateNfcUseCase;
    private final GetNfcHistoryUseCase getNfcHistoryUseCase;
    private final GetActiveNfcUseCase getActiveNfcUseCase;
    private final ScanNfcUseCase scanNfcUseCase;

    /**
     * Assign NFC to a student.
     */
    public StudentNfcResponse assignNfc(UUID studentId, AssignNfcRequest request, UUID assignedBy) {
        log.debug("Assigning NFC to student: {}", studentId);
        return assignNfcUseCase.execute(new AssignNfcUseCaseRequest(studentId, request, assignedBy));
    }

    /**
     * Deactivate NFC for a student.
     */
    public void deactivateNfc(UUID studentId) {
        log.debug("Deactivating NFC for student: {}", studentId);
        deactivateNfcUseCase.execute(studentId);
    }

    /**
     * Get NFC history for a student.
     */
    public List<StudentNfcResponse> getNfcHistory(UUID studentId) {
        log.debug("Getting NFC history for student: {}", studentId);
        return getNfcHistoryUseCase.execute(studentId);
    }

    /**
     * Get active NFC for a student.
     */
    public StudentNfcResponse getActiveNfc(UUID studentId) {
        log.debug("Getting active NFC for student: {}", studentId);
        return getActiveNfcUseCase.execute(studentId);
    }

    /**
     * Scan NFC to find student.
     */
    public StudentNfcResponse scanNfc(NfcScanRequest request) {
        log.debug("Scanning NFC: {}", request.nfcUid());
        return scanNfcUseCase.execute(request);
    }
}
package com.saferoute.guardian.adapter;

import com.saferoute.common.dto.guardian.GuardianRequest;
import com.saferoute.common.dto.guardian.GuardianResponse;
import com.saferoute.guardian.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Guardian Adapter - orchestrates use cases for guardian management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GuardianAdapter {

    private final GetAllGuardiansUseCase getAllGuardiansUseCase;
    private final GetGuardianByIdUseCase getGuardianByIdUseCase;
    private final CreateGuardianUseCase createGuardianUseCase;
    private final UpdateGuardianUseCase updateGuardianUseCase;
    private final DeleteGuardianUseCase deleteGuardianUseCase;
    private final UpdateFcmTokenUseCase updateFcmTokenUseCase;

    /**
     * Get all guardians.
     */
    public List<GuardianResponse> getAll() {
        return getAllGuardiansUseCase.execute(null);
    }

    /**
     * Get guardian by ID.
     */
    public GuardianResponse getById(UUID id) {
        return getGuardianByIdUseCase.execute(id);
    }

    /**
     * Create a new guardian.
     */
    public GuardianResponse create(GuardianRequest request) {
        return createGuardianUseCase.execute(request);
    }

    /**
     * Update an existing guardian.
     */
    public GuardianResponse update(UUID id, GuardianRequest request) {
        return updateGuardianUseCase.execute(
            new com.saferoute.guardian.usecase.UpdateGuardianRequest(id, request)
        );
    }

    /**
     * Delete a guardian.
     */
    public void delete(UUID id) {
        deleteGuardianUseCase.execute(id);
    }

    /**
     * Update FCM token.
     */
    public GuardianResponse updateFcmToken(UUID id, String token) {
        return updateFcmTokenUseCase.execute(
            new com.saferoute.guardian.usecase.UpdateFcmTokenRequest(id, token)
        );
    }
}

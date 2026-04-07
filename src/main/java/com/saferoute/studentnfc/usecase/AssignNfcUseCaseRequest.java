package com.saferoute.studentnfc.usecase;

import java.util.UUID;

/**
 * Request for AssignNfcUseCase
 */
public record AssignNfcUseCaseRequest(
    UUID studentId,
    com.saferoute.studentnfc.dto.AssignNfcRequest request,
    UUID assignedBy
) {}
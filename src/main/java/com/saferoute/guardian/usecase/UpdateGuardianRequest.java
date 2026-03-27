package com.saferoute.guardian.usecase;

import com.saferoute.common.dto.guardian.GuardianRequest;

import java.util.UUID;

/**
 * Request record for updating a guardian.
 */
public record UpdateGuardianRequest(UUID id, GuardianRequest request) {}

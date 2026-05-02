package com.saferoute.guardian.usecase.request;

import java.util.UUID;

public record DeleteGuardianStudentRequest(UUID guardianId, UUID studentId) {}
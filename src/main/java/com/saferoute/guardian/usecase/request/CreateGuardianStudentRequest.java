package com.saferoute.guardian.usecase.request;

import com.saferoute.guardian.dto.GuardianStudentData;
import java.util.UUID;

public record CreateGuardianStudentRequest(UUID guardianId, GuardianStudentData studentData) {}
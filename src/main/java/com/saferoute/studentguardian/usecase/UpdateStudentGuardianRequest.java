package com.saferoute.studentguardian.usecase;

import com.saferoute.common.dto.studentguardian.StudentGuardianRequest;

import java.util.UUID;

public record UpdateStudentGuardianRequest(UUID id, StudentGuardianRequest request) {}

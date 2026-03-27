package com.saferoute.common.dto.studentguardian;

import com.saferoute.common.entity.StudentGuardianEntity.Relationship;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentGuardianResponse(
    UUID id,
    UUID studentId,
    String studentName,
    UUID guardianId,
    String guardianName,
    Relationship relationship,
    Boolean isEmergencyContact,
    Boolean notifyEvents,
    LocalDateTime createdAt
) {}

package com.saferoute.common.dto.studentguardian;

import com.saferoute.common.entity.StudentGuardianEntity.Relationship;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StudentGuardianRequest(
    @NotNull(message = "El ID del estudiante es obligatorio")
    UUID studentId,

    @NotNull(message = "El ID del acudiente es obligatorio")
    UUID guardianId,

    @NotNull(message = "La relación es obligatoria")
    Relationship relationship,

    Boolean isEmergencyContact,

    Boolean notifyEvents
) {}

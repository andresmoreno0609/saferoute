package com.saferoute.common.dto.driver;

import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.dto.vehicle.VehicleResponse;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record DriverResponse(
    UUID id,
    UUID userId,
    String name,
    String phone,
    String documentNumber,
    LocalDate birthDate,
    String address,
    String licenseNumber,
    String licenseCategory,
    LocalDate licenseExpirationDate,
    String emergencyContact,
    String emergencyPhone,
    Integer yearsExperience,
    String photoUrl,
    String bankName,
    String bankAccount,
    UUID vehicleId,
    VehicleResponse vehicle,
    List<DriverDocumentResponse> documents,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

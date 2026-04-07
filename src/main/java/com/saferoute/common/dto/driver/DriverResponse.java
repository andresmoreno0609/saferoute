package com.saferoute.common.dto.driver;

import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.dto.vehicle.VehicleResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
) {
    public static DriverResponseBuilder builder() {
        return new DriverResponseBuilder();
    }

    public static class DriverResponseBuilder {
        private UUID id;
        private UUID userId;
        private String name;
        private String phone;
        private String documentNumber;
        private LocalDate birthDate;
        private String address;
        private String licenseNumber;
        private String licenseCategory;
        private LocalDate licenseExpirationDate;
        private String emergencyContact;
        private String emergencyPhone;
        private Integer yearsExperience;
        private String photoUrl;
        private String bankName;
        private String bankAccount;
        private UUID vehicleId;
        private VehicleResponse vehicle;
        private List<DriverDocumentResponse> documents;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DriverResponseBuilder id(UUID id) { this.id = id; return this; }
        public DriverResponseBuilder userId(UUID userId) { this.userId = userId; return this; }
        public DriverResponseBuilder name(String name) { this.name = name; return this; }
        public DriverResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public DriverResponseBuilder documentNumber(String documentNumber) { this.documentNumber = documentNumber; return this; }
        public DriverResponseBuilder birthDate(LocalDate birthDate) { this.birthDate = birthDate; return this; }
        public DriverResponseBuilder address(String address) { this.address = address; return this; }
        public DriverResponseBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public DriverResponseBuilder licenseCategory(String licenseCategory) { this.licenseCategory = licenseCategory; return this; }
        public DriverResponseBuilder licenseExpirationDate(LocalDate licenseExpirationDate) { this.licenseExpirationDate = licenseExpirationDate; return this; }
        public DriverResponseBuilder emergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; return this; }
        public DriverResponseBuilder emergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; return this; }
        public DriverResponseBuilder yearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; return this; }
        public DriverResponseBuilder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public DriverResponseBuilder bankName(String bankName) { this.bankName = bankName; return this; }
        public DriverResponseBuilder bankAccount(String bankAccount) { this.bankAccount = bankAccount; return this; }
        public DriverResponseBuilder vehicleId(UUID vehicleId) { this.vehicleId = vehicleId; return this; }
        public DriverResponseBuilder vehicle(VehicleResponse vehicle) { this.vehicle = vehicle; return this; }
        public DriverResponseBuilder documents(List<DriverDocumentResponse> documents) { this.documents = documents; return this; }
        public DriverResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DriverResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public DriverResponse build() {
            return new DriverResponse(id, userId, name, phone, documentNumber, birthDate, address, 
                licenseNumber, licenseCategory, licenseExpirationDate, emergencyContact, emergencyPhone,
                yearsExperience, photoUrl, bankName, bankAccount, vehicleId, vehicle, documents, 
                createdAt, updatedAt);
        }
    }
}

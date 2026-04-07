package com.saferoute.common.dto.driver;

import com.saferoute.common.dto.driverdocument.DriverDocumentResponse;
import com.saferoute.common.dto.vehicle.VehicleResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DriverResponse(
    UUID id,
    UUID userId,
    String name,
    String phone,
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
        private UUID vehicleId;
        private VehicleResponse vehicle;
        private List<DriverDocumentResponse> documents;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DriverResponseBuilder id(UUID id) { this.id = id; return this; }
        public DriverResponseBuilder userId(UUID userId) { this.userId = userId; return this; }
        public DriverResponseBuilder name(String name) { this.name = name; return this; }
        public DriverResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public DriverResponseBuilder vehicleId(UUID vehicleId) { this.vehicleId = vehicleId; return this; }
        public DriverResponseBuilder vehicle(VehicleResponse vehicle) { this.vehicle = vehicle; return this; }
        public DriverResponseBuilder documents(List<DriverDocumentResponse> documents) { this.documents = documents; return this; }
        public DriverResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DriverResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public DriverResponse build() {
            return new DriverResponse(id, userId, name, phone, vehicleId, vehicle, documents, createdAt, updatedAt);
        }
    }
}

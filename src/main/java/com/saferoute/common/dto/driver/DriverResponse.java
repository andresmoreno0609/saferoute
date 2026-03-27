package com.saferoute.common.dto.driver;

import java.time.LocalDateTime;
import java.util.UUID;

public record DriverResponse(
    UUID id,
    UUID userId,
    String name,
    String phone,
    String vehiclePlate,
    String vehicleModel,
    String vehicleColor,
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
        private String vehiclePlate;
        private String vehicleModel;
        private String vehicleColor;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DriverResponseBuilder id(UUID id) { this.id = id; return this; }
        public DriverResponseBuilder userId(UUID userId) { this.userId = userId; return this; }
        public DriverResponseBuilder name(String name) { this.name = name; return this; }
        public DriverResponseBuilder phone(String phone) { this.phone = phone; return this; }
        public DriverResponseBuilder vehiclePlate(String vehiclePlate) { this.vehiclePlate = vehiclePlate; return this; }
        public DriverResponseBuilder vehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; return this; }
        public DriverResponseBuilder vehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; return this; }
        public DriverResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DriverResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public DriverResponse build() {
            return new DriverResponse(id, userId, name, phone, vehiclePlate, vehicleModel, vehicleColor, createdAt, updatedAt);
        }
    }
}

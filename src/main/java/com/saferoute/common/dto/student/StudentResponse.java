package com.saferoute.common.dto.student;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentResponse(
    UUID id,
    String name,
    String address,
    Double homeLatitude,
    Double homeLongitude,
    String schoolName,
    Double schoolLatitude,
    Double schoolLongitude,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static StudentResponseBuilder builder() {
        return new StudentResponseBuilder();
    }

    public static class StudentResponseBuilder {
        private UUID id;
        private String name;
        private String address;
        private Double homeLatitude;
        private Double homeLongitude;
        private String schoolName;
        private Double schoolLatitude;
        private Double schoolLongitude;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentResponseBuilder id(UUID id) { this.id = id; return this; }
        public StudentResponseBuilder name(String name) { this.name = name; return this; }
        public StudentResponseBuilder address(String address) { this.address = address; return this; }
        public StudentResponseBuilder homeLatitude(Double homeLatitude) { this.homeLatitude = homeLatitude; return this; }
        public StudentResponseBuilder homeLongitude(Double homeLongitude) { this.homeLongitude = homeLongitude; return this; }
        public StudentResponseBuilder schoolName(String schoolName) { this.schoolName = schoolName; return this; }
        public StudentResponseBuilder schoolLatitude(Double schoolLatitude) { this.schoolLatitude = schoolLatitude; return this; }
        public StudentResponseBuilder schoolLongitude(Double schoolLongitude) { this.schoolLongitude = schoolLongitude; return this; }
        public StudentResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentResponse build() {
            return new StudentResponse(id, name, address, homeLatitude, homeLongitude, schoolName, schoolLatitude, schoolLongitude, createdAt, updatedAt);
        }
    }
}

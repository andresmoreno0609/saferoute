package com.saferoute.common.entity;

import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, columnDefinition = "GEOGRAPHY(POINT,4326)")
    private Point location;

    @Column(name = "school_name", length = 255)
    private String schoolName;

    @Column(name = "school_location", columnDefinition = "GEOGRAPHY(POINT,4326)")
    private Point schoolLocation;

    @Column(name = "address_geocoded")
    @Builder.Default
    private Boolean addressGeocoded = false;

    @Column(name = "geocode_error", length = 500)
    private String geocodeError;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "grade", length = 50)
    private String grade;

    @Column(name = "emergency_contact", length = 255)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "medical_info", length = 1000)
    private String medicalInfo;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "student_code", length = 50)
    private String studentCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

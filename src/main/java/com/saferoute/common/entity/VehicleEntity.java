package com.saferoute.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "plate", nullable = false, unique = true, length = 20)
    private String plate;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "capacity")
    private Integer capacity;

    @OneToOne(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private DriverEntity driver;

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
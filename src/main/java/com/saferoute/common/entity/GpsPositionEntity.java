package com.saferoute.common.entity;

import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gps_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private RouteEntity route;

    @Column(nullable = false, columnDefinition = "GEOGRAPHY(POINT,4326)")
    private Point location;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private Double speed;

    @Column
    private Double heading;

    @Column
    private Double accuracy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

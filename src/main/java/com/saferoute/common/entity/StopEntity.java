package com.saferoute.common.entity;

import jakarta.persistence.*;
import lombok.*;

import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private RouteEntity route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(nullable = false)
    private Integer orderNum;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pickedUp = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean droppedOff = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

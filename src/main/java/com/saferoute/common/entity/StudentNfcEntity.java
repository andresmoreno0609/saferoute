package com.saferoute.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_nfc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentNfcEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @Column(name = "nfc_uid", length = 100, nullable = false, unique = true)
    private String nfcUid;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
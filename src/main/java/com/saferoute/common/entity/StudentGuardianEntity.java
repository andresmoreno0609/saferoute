package com.saferoute.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_guardians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGuardianEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private GuardianEntity guardian;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Relationship relationship;

    @Column(name = "is_emergency_contact", nullable = false)
    @Builder.Default
    private Boolean isEmergencyContact = false;

    @Column(name = "notify_events", nullable = false)
    @Builder.Default
    private Boolean notifyEvents = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Relationship {
        father, mother, guardian, other;

        @JsonCreator
        public static Relationship fromValue(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            String normalized = value.trim().toLowerCase();
            return switch (normalized) {
                case "padre", "father" -> father;
                case "madre", "mother" -> mother;
                case "acudiente", "guardian" -> guardian;
                case "otro", "other" -> other;
                default -> Relationship.valueOf(normalized);
            };
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

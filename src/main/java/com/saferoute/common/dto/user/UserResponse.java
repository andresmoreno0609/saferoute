package com.saferoute.common.dto.user;

import com.saferoute.common.entity.UserEntity.UserRole;
import com.saferoute.common.entity.UserEntity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String name,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime lastLoginAt
) {
    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
    }

    public static class UserResponseBuilder {
        private UUID id;
        private String email;
        private String name;
        private UserRole role;
        private UserStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;

        public UserResponseBuilder id(UUID id) { this.id = id; return this; }
        public UserResponseBuilder email(String email) { this.email = email; return this; }
        public UserResponseBuilder name(String name) { this.name = name; return this; }
        public UserResponseBuilder role(UserRole role) { this.role = role; return this; }
        public UserResponseBuilder status(UserStatus status) { this.status = status; return this; }
        public UserResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserResponseBuilder lastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; return this; }

        public UserResponse build() {
            return new UserResponse(id, email, name, role, status, createdAt, lastLoginAt);
        }
    }
}

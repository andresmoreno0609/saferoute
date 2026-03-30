package com.saferoute.common.service;

import com.saferoute.common.dto.notification.NotificationRequest;
import com.saferoute.common.dto.notification.NotificationResponse;
import com.saferoute.common.entity.*;
import com.saferoute.common.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final GuardianRepository guardianRepository;

    public NotificationResponse create(NotificationRequest request) {
        GuardianEntity guardian = guardianRepository.findById(request.guardianId())
                .orElseThrow(() -> new GuardianNotFoundException("Guardian not found with id: " + request.guardianId()));

        NotificationEntity entity = NotificationEntity.builder()
                .guardian(guardian)
                .title(request.title())
                .body(request.body())
                .type(request.type() != null ? request.type() : NotificationEntity.NotificationType.BOARD)
                .referenceId(request.referenceId())
                .build();

        NotificationEntity saved = notificationRepository.save(entity);
        log.info("Notification created: {} - {}", saved.getId(), saved.getType());
        
        // TODO: Integrate with FCM to send push notification
        // sendPushNotification(saved);
        
        return toResponse(saved);
    }

    public List<NotificationResponse> findAll() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public NotificationResponse findById(UUID id) {
        return notificationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }

    public List<NotificationResponse> findByGuardianId(UUID guardianId) {
        return notificationRepository.findByGuardianIdOrderBySentAtDesc(guardianId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> findUnreadByGuardianId(UUID guardianId) {
        return notificationRepository.findByGuardianIdAndReadAtIsNullOrderBySentAtDesc(guardianId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public NotificationResponse markAsRead(UUID id) {
        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
        
        entity.setReadAt(LocalDateTime.now());
        NotificationEntity saved = notificationRepository.save(entity);
        log.info("Notification marked as read: {}", id);
        return toResponse(saved);
    }

    public int markAllAsRead(UUID guardianId) {
        List<NotificationEntity> unread = notificationRepository.findByGuardianIdAndReadAtIsNullOrderBySentAtDesc(guardianId);
        LocalDateTime now = LocalDateTime.now();
        
        for (NotificationEntity entity : unread) {
            entity.setReadAt(now);
        }
        
        List<NotificationEntity> saved = notificationRepository.saveAll(unread);
        log.info("Marked {} notifications as read for guardian: {}", saved.size(), guardianId);
        return saved.size();
    }

    private NotificationResponse toResponse(NotificationEntity entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getGuardian().getId(),
                entity.getGuardian().getName(),
                entity.getGuardian().getEmail(),
                entity.getTitle(),
                entity.getBody(),
                entity.getType(),
                entity.getReferenceId(),
                entity.getSentAt(),
                entity.getReadAt(),
                entity.getReadAt() != null
        );
    }

    public static class GuardianNotFoundException extends RuntimeException {
        public GuardianNotFoundException(String message) { super(message); }
    }

    public static class NotificationNotFoundException extends RuntimeException {
        public NotificationNotFoundException(String message) { super(message); }
    }
}

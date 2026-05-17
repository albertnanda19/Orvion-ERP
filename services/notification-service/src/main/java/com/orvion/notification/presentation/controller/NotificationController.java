package com.orvion.notification.presentation.controller;

import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.notification.domain.model.NotificationLog;
import com.orvion.notification.domain.model.enums.NotificationStatus;
import com.orvion.notification.domain.repository.NotificationLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "User notification endpoints")
public class NotificationController extends BaseController {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationController(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @GetMapping
    @Operation(summary = "Get notifications for current user")
    @PreAuthorize("isAuthenticated()")
    public List<NotificationLog> getNotifications() {
        String tenantId = extractTenantId();
        String userId = extractUserId();
        return notificationLogRepository.findByTenantIdAndRecipientId(tenantId, userId);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Long> getUnreadCount() {
        String tenantId = extractTenantId();
        String userId = extractUserId();
        long count = notificationLogRepository.countByTenantIdAndRecipientIdAndStatus(
                tenantId, userId, NotificationStatus.SENT);
        return Map.of("count", count);
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read")
    @PreAuthorize("isAuthenticated()")
    public void markAsRead(@PathVariable UUID notificationId) {
        NotificationLog log = notificationLogRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        log.setStatus(NotificationStatus.SENT);
        notificationLogRepository.save(log);
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    @PreAuthorize("isAuthenticated()")
    public void markAllAsRead() {
        String tenantId = extractTenantId();
        String userId = extractUserId();
        List<NotificationLog> unread = notificationLogRepository
                .findByTenantIdAndRecipientIdAndStatus(tenantId, userId, NotificationStatus.SENT);
        unread.forEach(n -> n.setStatus(NotificationStatus.SENT));
        unread.forEach(notificationLogRepository::save);
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a notification")
    @PreAuthorize("isAuthenticated()")
    public void deleteNotification(@PathVariable UUID notificationId) {
        notificationLogRepository.deleteById(notificationId);
    }
}

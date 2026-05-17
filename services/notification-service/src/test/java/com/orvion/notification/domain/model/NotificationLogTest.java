package com.orvion.notification.domain.model;

import com.orvion.notification.domain.model.enums.NotificationChannel;
import com.orvion.notification.domain.model.enums.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class NotificationLogTest {

    private NotificationLog logEntry;

    @BeforeEach
    void setUp() {
        logEntry = new NotificationLog(
                "tenant1",
                "user-001",
                "user@example.com",
                NotificationChannel.EMAIL,
                "Invoice Created",
                "<html><body>Invoice body</body></html>",
                NotificationStatus.SENT,
                "event-uuid-123",
                "orvion.finance.invoice.created"
        );
    }

    @Test
    void testCreateNotificationLog() {
        assertNotNull(logEntry.getId());
        assertEquals("tenant1", logEntry.getTenantId());
        assertEquals("user-001", logEntry.getRecipientId());
        assertEquals("user@example.com", logEntry.getRecipientEmail());
        assertEquals(NotificationChannel.EMAIL, logEntry.getChannel());
        assertEquals("Invoice Created", logEntry.getSubject());
        assertEquals(NotificationStatus.SENT, logEntry.getStatus());
        assertEquals("event-uuid-123", logEntry.getEventId());
    }

    @Test
    void testSetSentAt() {
        Instant now = Instant.now();
        logEntry.setSentAt(now);
        assertEquals(now, logEntry.getSentAt());
    }

    @Test
    void testSetErrorMessage() {
        logEntry.setErrorMessage("Connection timeout");
        assertEquals("Connection timeout", logEntry.getErrorMessage());
    }

    @Test
    void testSetStatusFailed() {
        logEntry.setStatus(NotificationStatus.FAILED);
        assertEquals(NotificationStatus.FAILED, logEntry.getStatus());
    }

    @Test
    void testSetStatusSkipped() {
        logEntry.setStatus(NotificationStatus.SKIPPED);
        assertEquals(NotificationStatus.SKIPPED, logEntry.getStatus());
    }

    @Test
    void testSetStatusPending() {
        logEntry.setStatus(NotificationStatus.PENDING);
        assertEquals(NotificationStatus.PENDING, logEntry.getStatus());
    }

    @Test
    void testInitialStatus() {
        NotificationLog pending = new NotificationLog(
                "tenant1", "user-002", null,
                NotificationChannel.PUSH, null, null,
                NotificationStatus.PENDING, "evt-002", "event.type"
        );
        assertEquals(NotificationStatus.PENDING, pending.getStatus());
        assertNull(pending.getBody());
        assertNull(pending.getSentAt());
    }

    @Test
    void testSendAtAfterSent() {
        logEntry.setSentAt(Instant.now());
        assertNotNull(logEntry.getSentAt());
    }
}

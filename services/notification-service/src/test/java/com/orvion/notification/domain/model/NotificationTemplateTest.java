package com.orvion.notification.domain.model;

import com.orvion.notification.domain.model.enums.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTemplateTest {

    private NotificationTemplate template;

    @BeforeEach
    void setUp() {
        template = new NotificationTemplate(
                "tenant1",
                "invoice-created",
                "Invoice #{invoiceNumber} Created",
                "<html><body><p th:text=\"${message}\">Hello</p></body></html>",
                NotificationChannel.EMAIL,
                "orvion.finance.invoice.created",
                "en"
        );
    }

    @Test
    void testCreateTemplate() {
        assertNotNull(template.getId());
        assertEquals("tenant1", template.getTenantId());
        assertEquals("invoice-created", template.getTemplateCode());
        assertEquals("Invoice #{invoiceNumber} Created", template.getSubject());
        assertEquals(NotificationChannel.EMAIL, template.getChannel());
        assertEquals("orvion.finance.invoice.created", template.getEventType());
        assertEquals("en", template.getLanguage());
        assertTrue(template.isActive());
    }

    @Test
    void testDeactivateTemplate() {
        template.setActive(false);
        assertFalse(template.isActive());
    }

    @Test
    void testUpdateSubject() {
        template.setSubject("Updated Invoice Subject");
        assertEquals("Updated Invoice Subject", template.getSubject());
    }

    @Test
    void testUpdateBody() {
        String newBody = "<html><body><p>Updated Body</p></body></html>";
        template.setBody(newBody);
        assertEquals(newBody, template.getBody());
    }

    @Test
    void testChangeChannel() {
        template.setChannel(NotificationChannel.IN_APP);
        assertEquals(NotificationChannel.IN_APP, template.getChannel());
    }

    @Test
    void testDefaultActive() {
        NotificationTemplate t = new NotificationTemplate(
                "tenant1", "code", "Subject", "Body",
                NotificationChannel.PUSH, "event.type", "en");
        assertTrue(t.isActive());
    }
}

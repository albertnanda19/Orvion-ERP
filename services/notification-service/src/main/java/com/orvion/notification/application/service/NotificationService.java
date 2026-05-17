package com.orvion.notification.application.service;

import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.notification.domain.model.NotificationLog;
import com.orvion.notification.domain.model.NotificationTemplate;
import com.orvion.notification.domain.model.enums.NotificationChannel;
import com.orvion.notification.domain.model.enums.NotificationStatus;
import com.orvion.notification.domain.repository.NotificationLogRepository;
import com.orvion.notification.domain.repository.NotificationTemplateRepository;
import com.orvion.notification.infrastructure.redis.NotificationRedisPublisher;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationTemplateRepository templateRepository;
    private final NotificationLogRepository logRepository;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final NotificationRedisPublisher redisPublisher;
    private final Counter sentCounter;
    private final Counter failedCounter;

    public NotificationService(NotificationTemplateRepository templateRepository,
                                NotificationLogRepository logRepository,
                                JavaMailSender mailSender,
                                SpringTemplateEngine templateEngine,
                                NotificationRedisPublisher redisPublisher,
                                @Qualifier("notificationsSentCounter") Counter notificationsSentCounter,
                                @Qualifier("notificationsFailedCounter") Counter notificationsFailedCounter) {
        this.templateRepository = templateRepository;
        this.logRepository = logRepository;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.redisPublisher = redisPublisher;
        this.sentCounter = notificationsSentCounter;
        this.failedCounter = notificationsFailedCounter;
    }

    @Transactional
    public void sendEmailNotification(String tenantId, String recipientEmail, String recipientId,
                                      String eventType, String eventId, Map<String, Object> templateData) {
        try {
            NotificationTemplate template = templateRepository
                .findByTenantIdAndEventTypeAndLanguageAndChannel(tenantId, eventType, "en", NotificationChannel.EMAIL)
                .orElse(null);

            if (template == null) {
                log.warn("No email template found for eventType={}, tenantId={}", eventType, tenantId);
                saveLog(tenantId, recipientId, recipientEmail, NotificationChannel.EMAIL,
                        null, null, NotificationStatus.SKIPPED, eventId, eventType,
                        "No template configured");
                return;
            }

            if (!template.isActive()) {
                log.warn("Template {} is inactive, skipping notification", template.getTemplateCode());
                saveLog(tenantId, recipientId, recipientEmail, NotificationChannel.EMAIL,
                        template.getSubject(), null, NotificationStatus.SKIPPED, eventId, eventType,
                        "Template inactive");
                return;
            }

            Context ctx = new Context();
            templateData.forEach(ctx::setVariable);
            String htmlBody = templateEngine.process(template.getTemplateCode(), ctx);

            sendMimeEmail(recipientEmail, template.getSubject(), htmlBody);

            saveLog(tenantId, recipientId, recipientEmail, NotificationChannel.EMAIL,
                    template.getSubject(), htmlBody, NotificationStatus.SENT, eventId, eventType, null);

            redisPublisher.publish(tenantId, recipientId, eventType, template.getSubject(), htmlBody);

            sentCounter.increment();
            log.info("Email sent to {} for event {}", recipientEmail, eventId);

        } catch (Exception e) {
            log.error("Failed to send email for event {}: {}", eventId, e.getMessage());
            saveLog(tenantId, recipientId, recipientEmail, NotificationChannel.EMAIL,
                    null, null, NotificationStatus.FAILED, eventId, eventType, e.getMessage());
            failedCounter.increment();
        }
    }

    private void sendMimeEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom("noreply@orvion.com");
            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private void saveLog(String tenantId, String recipientId, String recipientEmail,
                          NotificationChannel channel, String subject, String body,
                          NotificationStatus status, String eventId, String eventType,
                          String errorMessage) {
        NotificationLog logEntry = new NotificationLog(tenantId, recipientId, recipientEmail,
                channel, subject, body, status, eventId, eventType);
        logEntry.setErrorMessage(errorMessage);
        if (status == NotificationStatus.SENT) {
            logEntry.setSentAt(Instant.now());
        }
        logRepository.save(logEntry);
    }
}

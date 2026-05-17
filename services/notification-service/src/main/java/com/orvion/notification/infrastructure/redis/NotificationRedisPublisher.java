package com.orvion.notification.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationRedisPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationRedisPublisher.class);
    private static final String CHANNEL = "orvion:notifications";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    public NotificationRedisPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
    }

    public void publish(String tenantId, String recipientId, String eventType,
                        String subject, String body) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("tenantId", tenantId);
            message.put("recipientId", recipientId);
            message.put("eventType", eventType);
            message.put("subject", subject);
            message.put("body", body);
            message.put("timestamp", System.currentTimeMillis());

            redisTemplate.convertAndSend(CHANNEL, message);
            log.debug("Published notification to Redis channel {} for user {}", CHANNEL, recipientId);
        } catch (Exception e) {
            log.error("Failed to publish notification to Redis: {}", e.getMessage());
        }
    }
}

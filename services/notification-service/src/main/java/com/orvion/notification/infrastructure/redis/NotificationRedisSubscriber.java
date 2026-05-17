package com.orvion.notification.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orvion.notification.infrastructure.websocket.NotificationWebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotificationRedisSubscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationRedisSubscriber.class);
    private final NotificationWebSocketController webSocketController;
    private final ObjectMapper mapper;

    public NotificationRedisSubscriber(NotificationWebSocketController webSocketController, ObjectMapper mapper) {
        this.webSocketController = webSocketController;
        this.mapper = mapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = mapper.readValue(body, Map.class);

            String tenantId = (String) payload.get("tenantId");
            String recipientId = (String) payload.get("recipientId");

            if (recipientId != null) {
                webSocketController.sendToUser(tenantId, recipientId, "/queue/notifications", payload);
            }
            webSocketController.sendToTopic(tenantId, payload);
        } catch (Exception e) {
            log.error("Failed to process Redis notification: {}", e.getMessage());
        }
    }
}

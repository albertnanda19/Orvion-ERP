package com.orvion.notification.infrastructure.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.security.Principal;
import java.util.Map;

@Controller
public class NotificationWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketController.class);
    private final SimpMessageSendingOperations messagingTemplate;

    public NotificationWebSocketController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/notifications/subscribe")
    public void subscribe(@Payload Map<String, String> payload, Principal principal) {
        String tenantId = payload.getOrDefault("tenantId", "default");
        String userId = payload.getOrDefault("userId", principal != null ? principal.getName() : "anonymous");
        log.debug("User {} subscribed to notifications for tenant {}", userId, tenantId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes() != null) {
            log.debug("Client disconnected: {}", headerAccessor.getSessionId());
        }
    }

    public void sendToUser(String tenantId, String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }

    public void sendToTopic(String topic, Object payload) {
        messagingTemplate.convertAndSend("/topic/" + topic, payload);
    }
}

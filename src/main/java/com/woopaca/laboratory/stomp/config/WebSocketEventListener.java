package com.woopaca.laboratory.stomp.config;

import com.woopaca.laboratory.stomp.chat.ChatMessage;
import com.woopaca.laboratory.stomp.chat.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @EventListener
    public void handle(SessionSubscribeEvent subscribeEvent) {
        MessageHeaderAccessor messageHeaderAccessor = new MessageHeaderAccessor(subscribeEvent.getMessage());
        MessageHeaders messageHeaders = messageHeaderAccessor.getMessageHeaders();
        log.info("messageHeaders = {}", messageHeaders);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent disconnectEvent) {
        Message<byte[]> disconnectMessage = disconnectEvent.getMessage();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(disconnectMessage);
        String username = (String) headerAccessor.getSessionAttributes()
                .get("username");
        if (StringUtils.hasText(username)) {
            log.info("User disconnected: [{}]", username);
            ChatMessage chatMessage = ChatMessage.builder()
                    .messageType(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messageTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}

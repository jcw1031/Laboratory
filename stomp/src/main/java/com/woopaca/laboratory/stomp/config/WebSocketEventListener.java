package com.woopaca.laboratory.stomp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
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
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(subscribeEvent.getMessage(), StompHeaderAccessor.class);
        log.info("accessor: {}", accessor);
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(disconnectEvent.getMessage(), StompHeaderAccessor.class);
        log.info("WebSocketEventListener.handleSessionDisconnectEvent");
    }
}

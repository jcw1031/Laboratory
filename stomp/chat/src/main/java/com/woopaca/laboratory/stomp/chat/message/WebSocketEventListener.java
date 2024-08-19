package com.woopaca.laboratory.stomp.chat.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent subscribeEvent) {
        MessageHeaderAccessor messageHeaderAccessor = new MessageHeaderAccessor(subscribeEvent.getMessage());
        MessageHeaders messageHeaders = messageHeaderAccessor.getMessageHeaders();
        log.info("messageHeaders = {}", messageHeaders);
    }

    @EventListener
    public void handleUnsubscribe(SessionSubscribeEvent subscribeEvent) {
        MessageHeaderAccessor messageHeaderAccessor = new MessageHeaderAccessor(subscribeEvent.getMessage());
        MessageHeaders messageHeaders = messageHeaderAccessor.getMessageHeaders();
        log.info("messageHeaders = {}", messageHeaders);
    }
}

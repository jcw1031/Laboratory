package com.woopaca.laboratory.stomp.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    public ChatController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/send-message")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        log.info("chatMessage = {}", chatMessage);
        String fullContent = chatMessage.content();
        if (fullContent.startsWith("@")) {
            int firstWhitespace = fullContent.indexOf(' ');
            String mention = fullContent.substring(1, firstWhitespace);
            log.info("mention = {}", mention);
            String content = fullContent.substring(firstWhitespace + 1);
            String username = (String) headerAccessor.getSessionAttributes()
                    .get("username");
            log.info("username: {}", username);
            chatMessage = new ChatMessage(content, username, MessageType.CHAT);
//            messagingTemplate.convertAndSend(String.format("/queue/%s", mention), chatMessage);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", chatMessage);
//            messagingTemplate.convertAndSend(String.format("/queue/%s", username), chatMessage);
            return;
        }
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat/add-user")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, StompHeaderAccessor headerAccessor, Principal principal) {
        log.info("principal: {}", principal);
        // Add username in websocket session
        log.info("chatMessage = {}", chatMessage);
        String sessionId = SimpAttributesContextHolder.getAttributes()
                .getSessionId();
        log.info("sessionId = {}", sessionId);
        String username = (String) SimpAttributesContextHolder.getAttributes()
                .getAttribute("username");
        log.info("username: {}", username);
        return chatMessage;
    }
}

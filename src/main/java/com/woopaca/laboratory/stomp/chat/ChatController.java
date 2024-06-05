package com.woopaca.laboratory.stomp.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    public ChatController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/send-message")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info("chatMessage = {}", chatMessage);
        String fullContent = chatMessage.content();
        if (fullContent.startsWith("@")) {
            int firstWhitespace = fullContent.indexOf(' ');
            String mention = fullContent.substring(1, firstWhitespace);
            log.info("mention = {}", mention);
            String content = fullContent.substring(firstWhitespace + 1);
            chatMessage = new ChatMessage(content, chatMessage.sender(), MessageType.CHAT);
            messagingTemplate.convertAndSend(String.format("/queue/%s", mention), chatMessage);
            messagingTemplate.convertAndSend(String.format("/queue/%s", chatMessage.sender()), chatMessage);
            return;
        }
        messagingTemplate.convertAndSend("/topic/sdfs", chatMessage);
    }

    @MessageMapping("/chat/add-user")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in websocket session
        log.info("chatMessage = {}", chatMessage);
        headerAccessor.getSessionAttributes()
                .put("username", chatMessage.sender());
        return chatMessage;
    }
}

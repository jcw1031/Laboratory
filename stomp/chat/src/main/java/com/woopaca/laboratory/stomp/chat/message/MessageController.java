package com.woopaca.laboratory.stomp.chat.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class MessageController {

    private final SimpMessageSendingOperations messagingTemplate;

    public MessageController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/messages")
    public void sendMessage(@Payload MessageBody messageBody) {
        log.info("messageBody: {}", messageBody);
    }
}

package com.woopaca.laboratory.stomp.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woopaca.laboratory.stomp.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "${rabbitmq.database-queue-name}")
    public void messageListener(GenericMessage<String> message) throws JsonProcessingException {
        String payload = message.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        log.info("RabbitMQ 메시지 리스너");
        log.info("GenericMessage = {}", message);
        log.info("chatMessage = {}", chatMessage);
    }
}

package com.woopaca.laboratory.kafka.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@SpringBootTest
public class PublishMessagesTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void publishMessage() throws InterruptedException {
        String topic = "woopaca";
        String message = "테스트";
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                log.info("Sent message: [{}] with offset: [{}]", message, result.getRecordMetadata().offset());
                return;
            }
            log.warn("Unable to send message: [{}] due to: {}", message, throwable.getMessage());
        });

        Thread.sleep(100);
    }
}

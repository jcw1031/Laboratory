package com.woopaca.laboratory.kafka.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageListener {

    @KafkaListener(id = "spring-kafka-test1", topics = "my-first-topic", groupId = "laboratory", batch = "true", concurrency = "4")
    public void listen(String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("Received message in group 'laboratory': [{}]", message);
        log.info("Received message in partition: [{}]", partition);
    }

    @KafkaListener(topics = "test-topic", groupId = "test-group", concurrency = "2")
    public void listenTestTopic(@Payload String message,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("[Consumer-{}] Received: {} | partition={}, offset={}",
                Thread.currentThread().getName(), message, partition, offset);
    }
}

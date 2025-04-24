package com.woopaca.laboratory.kafka.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageListener {

    @KafkaListener(id = "spring-kafka-test1", topics = "my-first-topic", groupId = "laboratory")
    public void listen(String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("Received message in group 'laboratory': [{}]", message);
        log.info("Received message in partition: [{}]", partition);
    }
}

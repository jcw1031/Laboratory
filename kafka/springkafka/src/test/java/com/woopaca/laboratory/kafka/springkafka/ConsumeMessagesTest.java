package com.woopaca.laboratory.kafka.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@SpringBootTest
public class ConsumeMessagesTest {

    @Test
    void consumeMessage() throws InterruptedException {
        Thread.sleep(10_000);
    }

    @TestConfiguration
    static class ConsumerConfiguration {

        @Bean
        public WoopacaConsumer woopacaConsumer() {
            return new WoopacaConsumer();
        }
    }

    static class WoopacaConsumer {

        @KafkaListener(id = "consumer_test", topics = "woopaca")
        public void listen(String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
            log.info("Received message in group 'laboratory': [{}]", message);
            log.info("Received message in partition: [{}]", partition);
        }
    }
}

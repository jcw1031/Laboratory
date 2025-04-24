package com.woopaca.laboratory.kafka.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class KafkaMessageProduceScheduler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AtomicLong counter = new AtomicLong(0);

    public KafkaMessageProduceScheduler(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1_000L)
    public void produce() {
        long count = counter.incrementAndGet();
        String key = "key-" + (count % 5);
        log.info("partition: {}", Math.abs(key.hashCode()) % 2);
        String value = "지찬우" + count;
        log.info(" key: [{}], value: [{}]", key, value);
        kafkaTemplate.send("my-first-topic", key, value);
    }
}

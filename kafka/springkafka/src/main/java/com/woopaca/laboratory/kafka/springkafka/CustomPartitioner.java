package com.woopaca.laboratory.kafka.springkafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

@Slf4j
public class CustomPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        return 1;
    }

    @Override
    public void close() {
        log.info("close");
    }

    @Override
    public void configure(Map<String, ?> configs) {
        log.info("configure");
        configs.forEach((key, value) -> log.info("key: {}, value: {}", key, value));
    }
}

package com.woopaca.laboratory.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class Main {

    public static void main(String[] args) {
        Properties producerProperties = new Properties();
        // 사용자 정의 파티셔너
//        producerProperties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (final Producer<Object, Object> producer = new KafkaProducer<>(producerProperties)) {
            producer.send(new ProducerRecord<>("my-first-topic", "key", "지찬우"));
        }

        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "MyFirstConsumer");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.toString());

        try (final Consumer<Long, String> consumer = new KafkaConsumer<>(properties)) {
            String topic = "my-first-topic";

            // 구독
            consumer.subscribe(List.of(topic));

            // 파티션을 지정할 수 있음
            // 하지만 해당 방식은 모든 consumer가 해당 방식을 사용해야 함
//            consumer.assign(List.of(new TopicPartition(topic, 1)));
            consumer.poll(Duration.ofMinutes(1))
                    .forEach(consumerRecord -> log.info("consumerRecord: {}", consumerRecord));
        }
    }

    static class CustomPartitioner implements Partitioner {

        @Override
        public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
            return 0;
        }

        @Override
        public void close() {

        }

        @Override
        public void configure(Map<String, ?> configs) {

        }
    }
}

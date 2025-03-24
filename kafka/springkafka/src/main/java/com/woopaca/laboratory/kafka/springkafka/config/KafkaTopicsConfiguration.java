package com.woopaca.laboratory.kafka.springkafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        System.getProperty("user.home");

        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        URL.of(URI.create("http://www.holybible.or.kr/cgi/biblesrch.php?VR=99&QR=%C3%A2%BC%BC%B1%E2+5%C0%E5&OD="))
        HttpClient client = HttpClient.newBuilder()
                .build();
        client.

        return new NewTopic("woopaca", 1, (short) 1);
    }
}

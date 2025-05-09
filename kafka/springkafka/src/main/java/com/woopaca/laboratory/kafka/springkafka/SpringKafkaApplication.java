package com.woopaca.laboratory.kafka.springkafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringKafkaApplication.class, args);
    }
}

package com.woopaca.laboratory.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RedisApplication.class, args);

        Thread.currentThread().join();
    }

}

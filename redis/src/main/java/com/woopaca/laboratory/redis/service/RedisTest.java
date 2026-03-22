package com.woopaca.laboratory.redis.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisTest {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisTest(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() throws InterruptedException {
        for (int i = 0; i < 1_000; i++) {
            Thread.sleep(500);
            redisTemplate.opsForValue().get("hello");
        }
    }
}

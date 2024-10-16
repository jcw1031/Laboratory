package com.woopaca.laboratory.redis.pubsub;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisPublisher {

    private final RedisOperations<String, String> redisOperations;

    public RedisPublisher(RedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void publish(String channel, String message) {
        redisOperations.convertAndSend(channel, message);
    }
}

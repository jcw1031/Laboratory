package com.woopaca.laboratory.redis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@SpringBootTest
public class RedisPubSubTest {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private RedisMessageListenerContainer messageListenerContainer;

    @Test
    public void test() {
        messageListenerContainer.addMessageListener((message, pattern) -> {
            log.info("A channel");
            log.info("message: {}", message);
        }, new ChannelTopic("A"));
        messageListenerContainer.addMessageListener((message, pattern) -> {
            log.info("B channel");
            log.info("message: {}", message);
        }, new ChannelTopic("B"));

        redisPublisher.publish("A", "지찬우!");
        redisPublisher.publish("B", "ㅎㅇ");
        redisPublisher.publish("A", "안녕!");
    }
}

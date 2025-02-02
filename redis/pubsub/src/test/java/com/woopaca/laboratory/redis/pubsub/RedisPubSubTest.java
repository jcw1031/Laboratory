package com.woopaca.laboratory.redis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@SpringBootTest
public class RedisPubSubTest {

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private RedisMessageListenerContainer messageListenerContainer;

    @BeforeEach
    void setUp() {
        messageListenerContainer.addMessageListener((message, pattern) -> {
            log.info("eventListener A");
        }, new ChannelTopic("test"));
        messageListenerContainer.addMessageListener((message, pattern) -> {
            log.info("eventListener B");
        }, new ChannelTopic("test"));
        messageListenerContainer.addMessageListener((message, pattern) -> {
            log.info("eventListener C");
        }, new ChannelTopic("test"));
        messageListenerContainer.addMessageListener((message, pattern) -> {
            log.info("eventListener D");
        }, new PatternTopic("test*"));
    }

    @Test
    public void test() {
        redisPublisher.publish("test", "지찬우");
        redisPublisher.publish("test:a", "지찬우");
    }
}

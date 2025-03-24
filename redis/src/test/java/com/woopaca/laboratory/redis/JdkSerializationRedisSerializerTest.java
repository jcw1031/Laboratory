package com.woopaca.laboratory.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.ObjectStreamClass;
import java.util.Base64;

@Slf4j
@SpringBootTest
public class JdkSerializationRedisSerializerTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void serializeTest() {
        SerializeTarget serializeTarget = new SerializeTarget("지찬우", 26);

        RedisSerializer<Object> redisSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());

        byte[] serialize = redisSerializer.serialize(serializeTarget);
        log.info("serialize: {}", Base64.getEncoder().encodeToString(serialize));
        Object deserialize = redisSerializer.deserialize(serialize);

        ObjectStreamClass desc = ObjectStreamClass.lookup(SerializeTarget.class);
        long serialVersionUID = desc.getSerialVersionUID();
        log.info("serialVersionUID: {}", serialVersionUID);

        log.info("deserialize: {}", deserialize);
    }
}

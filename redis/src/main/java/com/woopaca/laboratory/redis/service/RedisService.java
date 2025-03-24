package com.woopaca.laboratory.redis.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, byte[], byte[]> hashOperations;
    private final HashMapper<Object, byte[], byte[]> hashMapper = new ObjectHashMapper();

    public RedisService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void test() {
        Map<byte[], byte[]> mappedHash = hashMapper.toHash(new Test("test3"));
        hashOperations.putAll("test:3", mappedHash);

        Map<byte[], byte[]> loadedHash = hashOperations.entries("test:1");
        Test test = (Test) hashMapper.fromHash(loadedHash);
        log.info("test: {}", test);
    }

    record Test(String name) {
    }
}

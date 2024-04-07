package com.woopaca.laboratory.pkstrategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Transactional
@Rollback(value = false)
@SpringBootTest
public class TestDomainTest {

    @Autowired
    private TestDomainRepository testDomainRepository;

    @Test
    void test() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        String currentThreadName = Thread.currentThread()
                .getName();
        log.info("currentThreadName = {}", currentThreadName);
        testDomainRepository.save(new TestDomain(currentThreadName));

        /*for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
            });
        }*/
    }
}

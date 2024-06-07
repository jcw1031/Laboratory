package com.woopaca.laboratory.transaction.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public abstract class ParallelTest {

    private ExecutorService executorService;
    private CountDownLatch latch;

    protected void executionParallel(Consumer<Integer> consumer, int totalCount) throws InterruptedException {
        executorService = Executors.newFixedThreadPool(totalCount);
        latch = new CountDownLatch(totalCount);

        for (int i = 0; i < totalCount; i++) {
            int index = i;
            executorService.submit(() -> {
                try {
                    consumer.accept(index);
                } catch (Exception e) {
                    log.error("Exception", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}

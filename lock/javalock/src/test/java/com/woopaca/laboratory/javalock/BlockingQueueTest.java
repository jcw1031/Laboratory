package com.woopaca.laboratory.javalock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class BlockingQueueTest {

    private final ExecutorService producersExecutor;
    private final ExecutorService consumersExecutor;
    private final ExecutorService produceExecutor;
    private final ExecutorService consumeExecutor;
    private final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(5);

    public BlockingQueueTest() {
        this.producersExecutor = Executors.newFixedThreadPool(5);
        this.consumersExecutor = Executors.newFixedThreadPool(3);
        this.produceExecutor = Executors.newSingleThreadExecutor();
        this.consumeExecutor = Executors.newSingleThreadExecutor();
    }

    @Test
    void produceConsumeTest() throws InterruptedException {
        CountDownLatch producerLatch = new CountDownLatch(30);
        CountDownLatch consumerLatch = new CountDownLatch(30);

        executeProduce(producerLatch);
        executeConsume(consumerLatch);

        consumerLatch.await();
    }

    private void executeProduce(CountDownLatch produceCountDown) {
        produceExecutor.execute(() -> {
            log.info("생산 시작");
            for (int i = 0; i < 30; i++) {
                final int number = i + 1;
                producersExecutor.execute(produce(number, produceCountDown));
            }

            try {
                produceCountDown.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("생산 종료");
        });
    }

    private void executeConsume(CountDownLatch consumeCountDown) {
        consumeExecutor.execute(() -> {
            log.info("소비 시작");

            while (consumeCountDown.getCount() > 0) {
                consumersExecutor.execute(consume(consumeCountDown));
            }
            log.info("소비 종료");
        });
    }

    private Runnable produce(int number, CountDownLatch produceCountDown) {
        return () -> {
            randomWait();

            try {
                blockingQueue.put(number);
                log.info("생산: {}", number);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            produceCountDown.countDown();
        };
    }

    private Runnable consume(CountDownLatch consumeCountDown) {
        return () -> {
            randomWait();

            try {
                int number = blockingQueue.take();
                log.info("소비: {}", number);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            consumeCountDown.countDown();
        };
    }

    private void randomWait() {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(300));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

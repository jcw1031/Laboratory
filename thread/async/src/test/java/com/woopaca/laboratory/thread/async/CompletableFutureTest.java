package com.woopaca.laboratory.thread.async;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
class CompletableFutureTest {

    Executor executor = Executors.newFixedThreadPool(10, runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("woopaca-" + thread.threadId());
        return thread;
    });

    @Test
    void testCompletableFuture() {
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            log.info("A");
            sleep(500);
            return "A";
        }, executor);
        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> {
            log.info("B");
            sleep(500);
            return "B";
        }, executor);
        CompletableFuture<String> futureC = CompletableFuture.supplyAsync(() -> {
            log.info("C");
            sleep(500);
            return "C";
        }, executor);

        // A, B, C 결과를 사용해 D 생성
        CompletableFuture<String> futureD = CompletableFuture.allOf(futureA, futureB, futureC)
                .thenCompose(unused -> {
                    String resultA = futureA.join();
                    String resultB = futureB.join();
                    String resultC = futureC.join();
                    log.info("{} {} {}", resultA, resultB, resultC);
                    return CompletableFuture.supplyAsync(() -> {
                        log.info("D");
                        sleep(500);
                        return "D";
                    }, executor);
                });

        CompletableFuture<String> futureE = CompletableFuture.supplyAsync(() -> {
            log.info("E");
            sleep(500);
            return "E";
        }, executor);

        CompletableFuture.allOf(futureD, futureE)
                .thenAccept(unused -> {
                    String resultD = futureD.join();
                    String resultE = futureE.join();
                    log.info("{} {}", resultD, resultE);
                })
                .join();
    }

    @Test
    void thenApplyAsyncTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1_000);

        ThreadPoolTaskExecutor mainTaskExecutor = new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(20)
                .maxPoolSize(20)
                .queueCapacity(1_000)
                .threadNamePrefix("main-")
                .build();
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(4)
                .maxPoolSize(4)
                .queueCapacity(10_000)
                .threadNamePrefix("async-")
                .build();
        asyncTaskExecutor.initialize();
        mainTaskExecutor.initialize();

        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> sleep(200), asyncTaskExecutor);
        CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(() -> sleep(200), asyncTaskExecutor);
        CompletableFuture<Void> completableFuture3 = CompletableFuture.runAsync(() -> sleep(200), asyncTaskExecutor);
        CompletableFuture<Void> completableFuture4 = CompletableFuture.runAsync(() -> sleep(200), asyncTaskExecutor);
        CompletableFuture<Void> completableFuture5 = CompletableFuture.runAsync(() -> sleep(200), asyncTaskExecutor);

        CompletableFuture<Void> completableFuture6 = completableFuture1.thenAcceptAsync(unused -> {
            log.info("6");
            sleep(500);
        }, asyncTaskExecutor);

        CompletableFuture<Void> completableFuture7 = CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3)
                .thenAcceptAsync(unused -> {
                    log.info("7");
                    sleep(500);
                }, asyncTaskExecutor);

        CompletableFuture<Void> completableFuture8 = CompletableFuture.allOf(completableFuture4, completableFuture5)
                .thenAcceptAsync(unused -> {
                    log.info("8");
                    sleep(500);
                }, asyncTaskExecutor);

        for (int i = 0; i < 1_000; i++) {
            mainTaskExecutor.execute(() -> {
                try {
                    CompletableFuture.allOf(completableFuture6, completableFuture7, completableFuture8)
                            .thenAccept(unused -> log.info("완료"))
                            .join();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.woopaca.laboratory.thread.async;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

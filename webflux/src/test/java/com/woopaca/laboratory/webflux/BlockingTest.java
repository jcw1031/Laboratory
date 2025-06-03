package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
class BlockingTest {

    private final RestClient restClient;
    private final ExecutorService callExecutor;

    public BlockingTest() {
        this.restClient = RestClient.create();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        log.info("availableProcessors: {}", availableProcessors);
        this.callExecutor = Executors.newFixedThreadPool(availableProcessors * 2);
    }

    @ValueSource(ints = 10)
    @ParameterizedTest
    void test(int count) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(count);
        try (ExecutorService executorService = Executors.newFixedThreadPool(count)) {
            for (int i = 0; i < count; i++) {
                executorService.execute(() -> {
                    try {
                        Thread.sleep(2_000L);
                        call();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
        }
        countDownLatch.await();
    }

    private void call() {
        log.info("call()");

        Random random = new Random();
        List<CompletableFuture> futures = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            URI uri = UriComponentsBuilder.fromUriString("https://run.mocky.io/v3/a2c0b5e0-096e-470d-9921-6d28d7f52d71")
                    .queryParam("mocky-delay", random.nextInt(50, 300) + "ms")
                    .build()
                    .toUri();

            int finalI = i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                String response = restClient.get()
                        .uri(uri)
                        .retrieve()
                        .body(String.class);
                log.info("{}", finalI + 1);
                return response;
            }, callExecutor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenRun(() -> {
                    log.info("모든 요청 성공");
                    try {
                        for (CompletableFuture<String> future : futures) {
                            log.info("Response: {}", future.get());
                        }
                    } catch (Exception e) {
                        log.error("Error occurred: {}", e.getMessage());
                    }
                }).join();
    }
}
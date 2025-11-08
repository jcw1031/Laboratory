package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

@Slf4j
public class BlockingVirtualThreadTest {

    private final RestClient restClient;
    private final ExecutorService httpExecutor;
    private final Random random;

    public BlockingVirtualThreadTest() {
        this.restClient = RestClient.create();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        log.info("availableProcessors: {}", availableProcessors);
        this.httpExecutor = Executors.newVirtualThreadPerTaskExecutor();
        this.random = new Random();
    }

    @ValueSource(ints = 100)
    @ParameterizedTest
    void test(int count) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(count);
        ThreadFactory threadFactory = Thread.ofPlatform()
                .name("caller-", 1)
                .factory();
        ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory);
        try (executorService) {
            for (int i = 0; i < count; i++) {
                int finalI = i;
                executorService.execute(() -> {
                    try {
                        call((char) ('A' + finalI));
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
        }

        countDownLatch.await();
    }

    private void call(char prefix) {
        log.info("call()");
        List<CompletableFuture<String>> futures = IntStream.rangeClosed(1, 20)
                .mapToObj(value -> requestAsync(prefix, value))
                .toList();

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
                })
                .join();
    }

    private CompletableFuture<String> requestAsync(char prefix, int value) {
        URI uri = UriComponentsBuilder.fromUriString("http://43.203.219.110:8080")
                .queryParam("delay", random.nextInt(50, 51))
                .build()
                .toUri();
        return CompletableFuture.supplyAsync(() -> {
            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
            log.info("{}-{}", prefix, value);
            return response;
        }, httpExecutor);
    }
}

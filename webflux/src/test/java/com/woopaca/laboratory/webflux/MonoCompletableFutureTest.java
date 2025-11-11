package com.woopaca.laboratory.webflux;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

@Slf4j
class MonoCompletableFutureTest {

    private final WebClient webClient;
    private final ExecutorService httpExecutor;

    public MonoCompletableFutureTest() {
        HttpClient httpClient = HttpClient.create()
                .runOn(new NioEventLoopGroup(4));
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.httpExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
                .mapToObj(value -> requestAsyncWithBlock(prefix, value))
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

    private CompletableFuture<String> requestAsyncWithBlock(char prefix, int value) {
        URI uri = UriComponentsBuilder.fromUriString("http://43.203.219.110:8080")
                .queryParam("delay", 500)
                .build()
                .toUri();
        log.info("{}-{}", prefix, value);
        return CompletableFuture.supplyAsync(() -> webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(),
                httpExecutor);
    }

    private CompletableFuture<String> requestAsyncWithToFuture(char prefix, int value) {
        URI uri = UriComponentsBuilder.fromUriString("http://43.203.219.110:8080")
                .queryParam("delay", 500)
                .build()
                .toUri();
        log.info("{}-{}", prefix, value);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .publishOn(Schedulers.fromExecutor(httpExecutor))
                .toFuture();
    }
}

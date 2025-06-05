package com.woopaca.laboratory.webflux;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

@Slf4j
class NonBlockingTest {

    private final WebClient webClient;
    private final Random random;

    public NonBlockingTest() {
        HttpClient httpClient = HttpClient.create()
                .runOn(new NioEventLoopGroup(2));
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.random = new Random();
    }

    @ValueSource(ints = 1)
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
//                        Thread.sleep(random.nextInt(400, 450));
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
        URI uri = UriComponentsBuilder.fromUriString("https://run.mocky.io/v3/a2c0b5e0-096e-470d-9921-6d28d7f52d71")
                .queryParam("mocky-delay", "500ms")
                .build()
                .toUri();
        List<Mono<String>> monos = IntStream.rangeClosed(1, 12)
                .mapToObj(value -> webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnSuccess(response -> {
                            log.info("{}-{}", prefix, value);
                        }))
                .toList();

        Mono.zip(monos, Arrays::asList)
                .doOnSuccess(zipped -> log.info("모든 요청 성공"))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(result -> {
                    result.forEach(response -> {
                        log.info("Response: {}", response);
                    });
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()))
                .block();
    }
}

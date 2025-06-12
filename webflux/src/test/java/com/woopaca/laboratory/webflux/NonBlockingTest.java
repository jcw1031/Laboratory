package com.woopaca.laboratory.webflux;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
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
                .runOn(new NioEventLoopGroup(1));
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.random = new Random();
    }

    @ValueSource(ints = 10)
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
        List<Mono<String>> monos = IntStream.rangeClosed(1, 20)
                .mapToObj(value -> {
                    URI uri = UriComponentsBuilder.fromUriString("http://43.203.219.110:8080")
                            .queryParam("delay", random.nextInt(50, 80))
                            .build()
                            .toUri();
                    return webClient.get()
                            .uri(uri)
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnSuccess(response -> {
                                log.info("{}-{}", prefix, value);
                            });
                })
                .toList();

        Mono.zip(monos, Arrays::asList)
                .doOnSuccess(zipped -> log.info("모든 요청 성공"))
                .flatMap(result -> {
                    result.forEach(response -> {
                        log.info("Response: {}", response);
                    });
                    return Mono.fromCallable(() -> {
                        Thread.sleep(2_000L);
                        return "blocking";
                    });
                })
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()))
                .block();
    }
}

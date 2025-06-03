package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
class NonBlockingTest {

    private final WebClient webClient;

    public NonBlockingTest() {
        this.webClient = WebClient.create();
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

        List<Mono<String>> monos = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            URI uri = UriComponentsBuilder.fromUriString("https://run.mocky.io/v3/a2c0b5e0-096e-470d-9921-6d28d7f52d71")
                    .queryParam("mocky-delay", random.nextInt(50, 300) + "ms")
                    .build()
                    .toUri();

            int finalI = i;
            Mono<String> mono = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> {
                        log.info("{}", finalI + 1);
                    });

            monos.add(mono);
        }

        Mono.zip(monos.get(0), monos.get(1), monos.get(2), monos.get(3), monos.get(4), monos.get(5), monos.get(6), monos.get(7))
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
package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class MonoZipTest {

    private final WebClient webClient;
    private final RestClient restClient;
    private final Random random;

    public MonoZipTest() {
        this.webClient = WebClient.create();
        this.restClient = RestClient.create();
        this.random = new Random();
    }

    @Test
    void test() {
        List<Mono<String>> firstMonos = createMonos("first", 1, 5);
        Mono.zip(firstMonos.get(0), firstMonos.get(1), firstMonos.get(2), firstMonos.get(3), firstMonos.get(4))
                .flatMap(firstZipped -> {
                    List<Mono<String>> secondMonos = createMonos("second", 6, 10);
                    return Mono.zip(secondMonos.get(0), secondMonos.get(1), secondMonos.get(2), secondMonos.get(3), secondMonos.get(4))
                            .flatMap(secondZipped -> {
                                Mono<String> blockingMono = Mono.fromCallable(() -> {
                                            Thread.sleep(1_000L);
                                            return "11";
                                        })
                                        .doOnSuccess(response -> {
                                            log.info("blocking call response");
                                        });
                                Mono<String> otherBlockingMono = Mono.fromCallable(() -> {
                                            Thread.sleep(1_000L);
                                            return "12";
                                        })
                                        .doOnSuccess(response -> {
                                            log.info("other blocking call response");
                                        }).subscribeOn(Schedulers.boundedElastic());
                                return Mono.zip(blockingMono, blockingMono, otherBlockingMono);
                            });
                })
                .block();
    }

    @Test
    void realtimeCartTest() {
        List<Mono<String>> firstMonos = createMonos("first", 1, 2);
    }

    private List<Mono<String>> createMonos(String prefix, int start, int end) {
        URI uri = UriComponentsBuilder.fromUriString("https://run.mocky.io/v3/a2c0b5e0-096e-470d-9921-6d28d7f52d71")
                .queryParam("mocky-delay", random.nextInt(50, 80) + "ms")
                .build()
                .toUri();

        return IntStream.rangeClosed(start, end)
                .mapToObj(value -> webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnSuccess(response -> {
                            log.info("{}-{}", prefix, value);
                        }))
                .toList();
    }
}

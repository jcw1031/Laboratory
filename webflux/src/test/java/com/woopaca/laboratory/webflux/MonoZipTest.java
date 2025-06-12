package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Arrays;
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
        Mono.zip(firstMonos, Arrays::asList)
                .flatMap(firstZipped -> {
                    List<Mono<String>> secondMonos = createMonos("second", 6, 10);
                    return Mono.zip(secondMonos, Arrays::asList)
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
    void monoFromCallable() {
        Mono<String> fromCallable = Mono.fromCallable(() -> {
            Thread.sleep(1_000L);
            return "Mono.fromCallable()";
        }).doOnSuccess(result -> log.info("{}", result));

        Mono.zip(createMonos("mono", 1, 2), Arrays::asList)
                .flatMap(zipped -> {
                    String t1 = (String) zipped.get(0);
                    String t2 = (String) zipped.get(1);
                    log.info("t1: {}, t2: {}", t1, t2);
                    List<Mono<String>> monos = createMonos("mono", 3, 4);
                    return Mono.zip(monos.get(0), monos.get(1), fromCallable)
                            .flatMap(innerZipped -> {
                                log.info("Inner zipped: {}", innerZipped);
                                return Mono.just("All done");
                            });
                })
                .block();
    }

    @ParameterizedTest(name = "{0}개 동시 요청")
    @ValueSource(ints = 1_320)
    public void simultaneousRequests(int count) {
        List<Mono<String>> monos = createMonos("simultaneous", 1, count);
        Mono.zip(monos, Arrays::asList)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(zipped -> log.info("모든 요청 성공"))
                .flatMap(result -> {
                    log.info("Result: {}", result);
                    return Mono.just("All done");
                })
                .block();
    }

    private List<Mono<String>> createMonos(String prefix, int start, int end) {
        return IntStream.rangeClosed(start, end)
                .mapToObj(value -> {
                    int delay = random.nextInt(100, 800);
                    URI uri = UriComponentsBuilder.fromUriString("http://43.203.219.110:8080")
                            .queryParam("delay", delay)
                            .build()
                            .toUri();
                    return webClient.get()
                            .uri(uri)
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnSuccess(response -> log.info("{}-{} {}ms", prefix, value, delay));
                })
                .toList();
    }
}

package com.woopaca.laboratory.webflux;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Slf4j
class EventLoopThreadBlockingTest {

    private final WebClient webClient;
    private final TaskExecutor taskExecutor;
    private final TaskExecutor blockingTaskExecutor;

    public EventLoopThreadBlockingTest() {
        HttpClient httpClient = HttpClient.create()
                .runOn(new NioEventLoopGroup(4));
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(50)
                .maxPoolSize(50)
                .threadNamePrefix("task-")
                .build();
        taskExecutor.initialize();
        this.taskExecutor = taskExecutor;

        ThreadPoolTaskExecutor blockingTaskExecutor = new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(20)
                .maxPoolSize(20)
                .threadNamePrefix("blocking-")
                .build();
        blockingTaskExecutor.initialize();
        this.blockingTaskExecutor = blockingTaskExecutor;
    }

    @Test
    void test() throws InterruptedException {
        int count = 200;
        CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            taskExecutor.execute(() -> {
                try {
                    Mono<String> mono1 = fetch();
                    Mono<String> mono2 = fetch();
                    Mono<String> mono3 = fetch();

                    Tuple2<String, String> result = Mono.zip(mono1, mono2, mono3)
                            .flatMap(zipped -> {
                                Mono<String> innerMono1 = blockingWithOtherThread();
                                Mono<String> innerMono2 = fetch();
                                return Mono.zip(innerMono1, innerMono2);
                            })
                            .block();
                    log.info("result: {}", result);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    private Mono<String> blocking() {
        Random random = new Random();
        return Mono.fromCallable(() -> {
            int delay = random.nextInt(70, 100);
            Thread.sleep(delay);
            String result = String.format("blocking %dms", delay);
            log.info("result: {}", result);
            return result;
        });
    }

    private Mono<String> blockingWithOtherThread() {
        Random random = new Random();
        return Mono.fromCallable(() -> {
            int delay = random.nextInt(70, 100);
            Thread.sleep(delay);
            String result = String.format("blocking with thread %dms", delay);
            log.info("result: {}", result);
            return result;
        }).subscribeOn(Schedulers.fromExecutor(blockingTaskExecutor));
    }

    private Mono<String> fetch() {
        Random random = new Random();
        URI uri = UriComponentsBuilder.fromUriString("http://43.203.219.110:8080")
                .queryParam("delay", random.nextInt(70, 100))
                .build()
                .toUri();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(result -> log.info("result: {}", result));
    }
}

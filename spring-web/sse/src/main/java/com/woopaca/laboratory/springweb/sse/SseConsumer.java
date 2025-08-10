package com.woopaca.laboratory.springweb.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SseConsumer {

    private final WebClient webClient;

    public SseConsumer() {
        this.webClient = WebClient.create("http://localhost:8080/sse-server");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("SseConsumer.onApplicationReady() - Application is ready, starting SSE consumer with delay");
        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS)
                .execute(this::consume);
    }

    private void consume() {
        log.info("SseConsumer.consume() - Starting to consume SSE events");
        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri("/stream-sse")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
                })
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientRequestException ||
                                             throwable instanceof ConnectException))
                .doOnError(error -> log.warn("Retrying SSE connection due to: {}", error.getMessage()));

        eventStream.subscribe(
                content -> log.info("Time: {} - event: name[{}], id [{}], content [{}]",
                        LocalTime.now(), content.event(), content.id(), content.data()),
                error -> log.error("Error receiving SSE", error),
                () -> log.info("SSE stream completed"));
    }
}

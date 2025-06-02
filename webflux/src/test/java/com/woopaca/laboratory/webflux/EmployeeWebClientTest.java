package com.woopaca.laboratory.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
class EmployeeWebClientTest {

    private final WebClient webClient = WebClient.create("https://dev.like-knu.com");

    @Test
    void name() throws InterruptedException {
        Mono<String> calendarMono = webClient.get()
                .uri("/api/schedule")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> announcementMono = webClient.get()
                .uri("/api/announcements/student-news")
                .retrieve()
                .bodyToMono(String.class);

        Mono.zip(calendarMono, announcementMono)
                .flatMap(result -> {
                    String calendar = result.getT1();
                    String announcement = result.getT2();

                    log.info("Calendar: {}", calendar);
                    log.info("Announcement: {}", announcement);
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage()))
                .subscribe();
    }
}
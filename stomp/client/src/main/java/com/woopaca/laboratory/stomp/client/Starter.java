package com.woopaca.laboratory.stomp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Starter implements CommandLineRunner {

    private final StompClient stompClient;

    public Starter(StompClient stompClient) {
        this.stompClient = stompClient;
    }

    @Override
    public void run(String... args) {
        log.info("asdfasdf");
        String uuid = UUID.randomUUID()
                .toString();
        StompSession session = stompClient.subscribe("/queue/messages/" + uuid);
        log.info("session: {}", session);

        try (ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()) {
            executorService.scheduleWithFixedDelay(() -> {
                stompClient.send(session);
            }, 0, 3, TimeUnit.SECONDS);
        }
    }
}

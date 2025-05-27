package com.woopaca.laboratory.virtualthread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
public class VirtualThreadTest {

    private HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void name() {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual()
                .name("test-virtual-", 0)
                .factory();

        ThreadFactory platformThreadFactory = Thread.ofPlatform()
                .name("test-platform-", 0)
                .factory();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(""))
                .GET()
                .build();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        int count = 1_000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        try (ExecutorService executorService = Executors.newFixedThreadPool(1_000, virtualThreadFactory)) {
            for (int i = 0; i < count; i++) {
                int finalI = i;
                executorService.execute(() -> {
                    try {
                        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                        log.info("count: {}", finalI);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            log.info("Active Threads: {}", threadMXBean.getThreadCount());
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

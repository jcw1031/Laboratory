package com.woopaca.laboratory.virtualthread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class VirtualThreadTest {

    @Test
    void name() throws InterruptedException {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100_000; i++) {
                executorService.execute(() -> {
                    ThreadGroup threadGroup = Thread.currentThread()
                            .getThreadGroup();
                    log.info("threadGroup: {}", threadGroup);
                });
            }
        }
    }
}

package com.woopaca.laboratory.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

@Slf4j
public class MySubscription implements Flow.Subscription {

    private final MyPublisher publisher;
    private final ExecutorService executorService;

    private boolean isCanceled = false;

    public MySubscription(MyPublisher publisher) {
        this.publisher = publisher;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public void request(long n) {
        log.info("MySubscription.request");
        if (isCanceled) {
            return;
        }

        executorService.execute(() -> {
            for (int i = 0; i < n; i++) {
                if (isCanceled) {
                    return;
                }
                publisher.notifySubscribers(new Message(i, "Message " + i));
            }
        });
    }

    @Override
    public void cancel() {
        log.info("MySubscription.cancel");
        isCanceled = true;
        executorService.shutdown();
    }
}

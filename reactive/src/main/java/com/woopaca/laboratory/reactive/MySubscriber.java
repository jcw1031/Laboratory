package com.woopaca.laboratory.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MySubscriber implements Flow.Subscriber<Message> {

    private final int bufferSize = 10;
    private final AtomicInteger processedItems = new AtomicInteger(0);

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        log.info("MySubscriber.onSubscribe");
        this.subscription = subscription;
        subscription.request(bufferSize);
    }

    @Override
    public void onNext(Message item) {
        log.info("MySubscriber.onNext: {}", item);
        if (processedItems.incrementAndGet() >= bufferSize) {
            log.info("Buffer full, canceling subscription");
            processedItems.set(0);
            subscription.request(2);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("MySubscriber.onError", throwable);
    }

    @Override
    public void onComplete() {
        log.info("MySubscriber.onComplete");
        log.info("All messages processed");
    }
}

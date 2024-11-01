package com.woopaca.laboratory.reactive;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

@Slf4j
public class MyPublisher implements Flow.Publisher<Message> {

    private final List<Flow.Subscriber<? super Message>> subscribers = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    public void subscribe(Flow.Subscriber<? super Message> subscriber) {
        log.info("MyPublisher.subscribe");
        subscribers.add(subscriber);
        subscriber.onSubscribe(new MySubscription(this));
    }

    public void notifySubscribers(Message message) {
        log.info("MyPublisher.notifySubscribers");
        subscribers.forEach(subscriber -> {
            executorService.execute(() -> subscriber.onNext(message));
        });
    }

    public void close() {
        log.info("MyPublisher.close");
        subscribers.forEach(subscriber -> {
            executorService.execute(subscriber::onComplete);
        });
        executorService.shutdown();
    }
}

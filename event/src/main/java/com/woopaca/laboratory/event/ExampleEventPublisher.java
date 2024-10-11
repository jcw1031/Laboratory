package com.woopaca.laboratory.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ExampleEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public ExampleEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishExampleEvent() {
        ExampleEvent event = new ExampleEvent();
        eventPublisher.publishEvent(event); // 이벤트 발행
    }
}

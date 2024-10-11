package com.woopaca.laboratory.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExampleEventSubscriber {

    @EventListener
    public void handleExampleEvent(ExampleEvent event) {
        // 이벤트 처리
    }
}

package com.woopaca.laboratory.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleEventSubscriber {

    @EventListener
    public void handleExampleEvent(ExampleEvent event) {
        // 이벤트 처리
        log.info("ExampleEventSubscriber.handleExampleEvent");
    }
}

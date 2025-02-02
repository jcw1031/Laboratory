package com.woopaca.laboratory.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExampleEventPublisherTest {

    @Autowired
    private ExampleEventPublisher exampleEventPublisher;

    @Test
    void test() {
        exampleEventPublisher.publishExampleEvent();
    }
}

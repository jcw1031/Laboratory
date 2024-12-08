package com.woopaca.laboratory.nio.eventloop;

import java.io.IOException;

public class EventLoopApplication {

    public static void main(String[] args) throws IOException {
        new EventLoop(8080)
                .run();
    }
}

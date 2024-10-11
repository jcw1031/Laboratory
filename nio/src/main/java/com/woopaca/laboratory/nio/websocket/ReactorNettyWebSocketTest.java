package com.woopaca.laboratory.nio.websocket;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import java.net.URI;

@Component
public class ReactorNettyWebSocketTest {

    private final WebSocketHandler webSocketHandler;

    public ReactorNettyWebSocketTest(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    public void connect() {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(URI.create("ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0"), webSocketHandler)
                .block();
    }
}

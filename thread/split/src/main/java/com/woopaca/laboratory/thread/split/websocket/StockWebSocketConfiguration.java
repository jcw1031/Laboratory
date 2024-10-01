package com.woopaca.laboratory.thread.split.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class StockWebSocketConfiguration {

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }
}

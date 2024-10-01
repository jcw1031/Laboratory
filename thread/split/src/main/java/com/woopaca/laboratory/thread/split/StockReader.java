package com.woopaca.laboratory.thread.split;

import com.woopaca.laboratory.thread.split.websocket.StockWebSocketHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;

import java.net.URI;

@Component
public class StockReader {

    private final WebSocketClient webSocketClient;
    private final StockWebSocketHandler stockWebSocketHandler;

    public StockReader(WebSocketClient webSocketClient, StockWebSocketHandler stockWebSocketHandler) {
        this.webSocketClient = webSocketClient;
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    @PostConstruct
    private void init() {
        URI uri = URI.create("ws://ops.koreainvestment.com:31000/tryitout/H0STCNT0");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        webSocketClient.execute(stockWebSocketHandler, headers, uri)
                .thenAccept(session -> {
                    try {
                        String payload = """
                                {
                                    "header": {
                                        "approval_key": "b7582ac5-4f1b-4545-adb9-396d5cc0d2e0",
                                        "custtype": "P",
                                        "tr_type": "1",
                                        "content-type": "utf-8"
                                    },
                                    "body": {
                                        "input": {
                                            "tr_id": "H0STCNT0",
                                            "tr_key": "000660"
                                        }
                                    }
                                }
                                """;
                        session.sendMessage(new TextMessage(payload));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
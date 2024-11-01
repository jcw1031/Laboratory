package com.woopaca.laboratory.thread.split;

import com.woopaca.laboratory.thread.split.websocket.StockWebSocketHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;

@Slf4j
@Component
public class StockReader {

    private final WebSocketClient webSocketClient;
    private final StockWebSocketHandler stockWebSocketHandler;

    public StockReader(WebSocketClient webSocketClient, StockWebSocketHandler stockWebSocketHandler) {
        this.webSocketClient = webSocketClient;
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    @PostConstruct
    private void init() throws IOException {
        WebSocketSession session = webSocketClient.execute(stockWebSocketHandler, "ws://ops.koreainvestment.com:21000/tryitout/HDFSCNT0")
                .join();
        String payload = """
                {
                    "header": {
                        "approval_key": "925532c7-8fe1-4b4c-b0c6-3288e7956341",
                        "custtype": "P",
                        "tr_type": "1",
                        "content-type": "utf-8"
                    },
                    "body": {
                        "input": {
                            "tr_id": "HDFSCNT0",
                            "tr_key": "DNASAAPL"
                        }
                    }
                }
                """;
        session.sendMessage(new TextMessage(payload));
        payload = """
                {
                    "header": {
                        "approval_key": "925532c7-8fe1-4b4c-b0c6-3288e7956341",
                        "custtype": "P",
                        "tr_type": "1",
                        "content-type": "utf-8"
                    },
                    "body": {
                        "input": {
                            "tr_id": "HDFSCNT0",
                            "tr_key": "DNASTSLA"
                        }
                    }
                }
                """;
        session.sendMessage(new TextMessage(payload));
        payload = """
                {
                    "header": {
                        "approval_key": "925532c7-8fe1-4b4c-b0c6-3288e7956341",
                        "custtype": "P",
                        "tr_type": "1",
                        "content-type": "utf-8"
                    },
                    "body": {
                        "input": {
                            "tr_id": "HDFSCNT0",
                            "tr_key": "DNASNVDA"
                        }
                    }
                }
                """;
        session.sendMessage(new TextMessage(payload));
        payload = """
                {
                    "header": {
                        "approval_key": "925532c7-8fe1-4b4c-b0c6-3288e7956341",
                        "custtype": "P",
                        "tr_type": "1",
                        "content-type": "utf-8"
                    },
                    "body": {
                        "input": {
                            "tr_id": "HDFSCNT0",
                            "tr_key": "DNASAMD"
                        }
                    }
                }
                """;
        session.sendMessage(new TextMessage(payload));
        payload = """
                {
                    "header": {
                        "approval_key": "925532c7-8fe1-4b4c-b0c6-3288e7956341",
                        "custtype": "P",
                        "tr_type": "1",
                        "content-type": "utf-8"
                    },
                    "body": {
                        "input": {
                            "tr_id": "HDFSCNT0",
                            "tr_key": "DNASMSFT"
                        }
                    }
                }
                """;
        session.sendMessage(new TextMessage(payload));
    }
}

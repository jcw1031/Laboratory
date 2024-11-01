package com.woopaca.laboratory.nio.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Mono<WebSocketMessage> initialMessage = Mono.just(session.textMessage("""
                {
                    "header": {
                        "approval_key": "4d352c98-9c24-4b64-a79a-df54e223fb3d",
                        "custtype": "P",
                        "tr_type": "1",
                        "content-type": "utf-8"
                    },
                    "body": {
                        "input": {
                            "tr_id": "H0STCNT0",
                            "tr_key": "DNASAAPL"
                        }
                    }
                }
                """));

        Mono<Void> sendInitialMessage = session.send(initialMessage);

        Flux<Void> handleMessages = session.receive()
                .flatMap(message -> {
                    String payload = message.getPayloadAsText();
                    log.info("Received message: {}", payload);
                    if (payload.contains("PINGPONG")) {
                        log.info("Sending PINGPONG response");
                        return session.send(Mono.just(session.textMessage(payload)));
                    }
                    return Mono.empty();
                });

        return sendInitialMessage.thenMany(handleMessages)
                .then();
    }
}
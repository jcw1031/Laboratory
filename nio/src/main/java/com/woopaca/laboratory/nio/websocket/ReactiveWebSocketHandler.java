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
        Mono<WebSocketMessage> mono = Mono.just(session.textMessage("""
                {
                    "header": {
                        "approval_key": "",
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
                """));
        Mono<Void> sendMessage = session.send(mono);
        Flux<Void> receiveMessages = session.receive()
                .doOnNext(message -> {

                    log.info("message: {}", message.getPayloadAsText());
                })
                .thenMany(Flux.never());

        return sendMessage.thenMany(receiveMessages)
                .then();
    }
}

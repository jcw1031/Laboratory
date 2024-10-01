package com.woopaca.laboratory.stomp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class StompClient {

    private final WebSocketStompClient stompClient;
    private final AtomicInteger sequece;

    public StompClient() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        stompClient.setTaskScheduler(new ThreadPoolTaskScheduler());
        stompClient.setDefaultHeartbeat(new long[]{5_000, 5_000});
        sequece = new AtomicInteger(0);
    }

    public StompSession subscribe(String destination) {
        StompSessionHandler sessionHandler = new CustomStompSessionHandler();
        try {
            StompSession session = stompClient.connectAsync("ws://localhost:8080/ws", sessionHandler)
                    .get();
            session.subscribe(destination, new CustomStompFrameHandler());
            return session;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(StompSession session) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination("/app/messages");
        headers.put(HttpHeaders.AUTHORIZATION, List.of("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqY3cwMDEwMzFAZ21haWwuY29tIiwiaXNzIjoidGF4aS1tYXRlLWxvY2FsIn0.jNc522eLKmZnNy-_nCPCD7mnMnVqbTjHVBveUV4pSiY"));
        String payload = String.format("""
                {
                	"partyId": 1,
                	"message": "%d번째 메시지"
                }
                """, sequece.incrementAndGet());
        session.send(headers, payload);
    }

    static class CustomStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("CustomStompSessionHandler.afterConnected");
        }
    }

    static class CustomStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            log.info("headers: {}, payload: {}", headers, payload);
        }
    }
}

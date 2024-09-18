package com.woopaca.laboratory.stomp.config;

import com.woopaca.laboratory.stomp.interceptor.CustomChannelInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .setUserDestinationPrefix("/user");
        registry.enableSimpleBroker("/topic", "/queue");
//        registry.setPreservePublishOrder(true);
//        registry.enableStompBrokerRelay("/topic", "/queue")
//                .setRelayHost("localhost")
//                .setRelayPort(61613)
//                .setClientLogin("chat")
//                .setClientPasscode("chat")
//                .setSystemLogin("chat")
//                .setSystemPasscode("chat");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new CustomChannelInterceptor());
    }
}

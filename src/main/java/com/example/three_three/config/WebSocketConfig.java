package com.example.three_three.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket에 연결할 때 사용할 엔드포인트를 등록합니다.
        // SockJS는 WebSocket을 지원하지 않는 브라우저에 대한 대체 옵션을 제공합니다.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커가 /topic으로 시작하는 주소를 구독하는 클라이언트에게 메시지를 전달하도록 설정합니다.
        registry.enableSimpleBroker("/topic");
        // 클라이언트가 메시지를 보낼 때 사용할 주소의 접두사를 /app으로 설정합니다.
        registry.setApplicationDestinationPrefixes("/app");
    }
}

package com.example.yetiproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "WebSocket")
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		//config.setApplicationDestinationPrefixes("/app"); //미정
		//config.enableSimpleBroker("/topic");

		config.enableSimpleBroker("/topic"); //subscription
		config.setApplicationDestinationPrefixes("/api/mytickets"); // pulication
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/reserve/queue/list")
			.setAllowedOriginPatterns("*")
			.withSockJS()
			.setClientLibraryUrl("<https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.3.0/sockjs.min.js>");
	}

}

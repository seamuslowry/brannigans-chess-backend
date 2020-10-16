package com.seamuslowry.branniganschess.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Component
@EnableWebSocketMessageBroker
class WebSocketConfig(
        @Value("\${cors.allowed-origin}") private val allowedOrigin: String
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(*allowedOrigin.split(",").toTypedArray())
                .withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker(MESSAGE_PREFIX)
        registry.setApplicationDestinationPrefixes(MESSAGE_PREFIX)
    }

    companion object {
        const val MESSAGE_PREFIX = "/game"
    }
}
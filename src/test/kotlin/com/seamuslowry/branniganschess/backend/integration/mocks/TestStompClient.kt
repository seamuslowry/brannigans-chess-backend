package com.seamuslowry.branniganschess.backend.integration.mocks

import org.springframework.stereotype.Component
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport

@Component
class TestStompClient : WebSocketStompClient(SockJsClient(
        listOf(WebSocketTransport(StandardWebSocketClient()))))
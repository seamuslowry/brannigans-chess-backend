package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompClient
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompSessionHandler
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.StompSession

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebSocketConnectionIntegrationTests(
        @LocalServerPort
        val port: Int,
        @Autowired val stompClient: TestStompClient
) {
    private lateinit var stompSession: StompSession

    @BeforeAll
    fun setUp() {
        val wsUrl = "ws://127.0.0.1:$port/ws"
        stompClient.messageConverter = StringMessageConverter()
        stompSession = stompClient.connect(wsUrl, TestStompSessionHandler()).get()
    }

    @AfterAll
    fun tearDown() {
        stompSession.disconnect()
        stompClient.stop()
    }

    @Test
    fun `connects to the socket`() {
        assertTrue(stompSession.isConnected)
    }
}
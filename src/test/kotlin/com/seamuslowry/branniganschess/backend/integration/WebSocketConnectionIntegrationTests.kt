package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompClient
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompSessionHandler
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.StompSession

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketConnectionIntegrationTests(
        @LocalServerPort
        val port: Int,
        @Autowired val stompClient: TestStompClient
) {
    private lateinit var stompSession: StompSession

    @BeforeEach
    fun setUp() {
        val wsUrl = "ws://127.0.0.1:$port/ws"
        stompClient.messageConverter = StringMessageConverter()
        stompSession = stompClient.connect(wsUrl, TestStompSessionHandler()).get()
    }

    @AfterEach
    fun tearDown() {
        stompSession.disconnect()
        stompClient.stop()
    }

    @Test
    fun `connects to the socket`() {
        assertTrue(stompSession.isConnected)
    }
}
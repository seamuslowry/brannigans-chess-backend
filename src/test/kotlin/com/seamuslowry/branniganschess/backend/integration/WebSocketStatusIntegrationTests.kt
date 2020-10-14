package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompClient
import org.awaitility.kotlin.await
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompFrameHandler
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompSessionHandler
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.services.GameService
import org.awaitility.kotlin.until
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
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
class WebSocketStatusIntegrationTests(
        @LocalServerPort
        val port: Int,
        @Autowired
        private val gameService: GameService,
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
    fun `gets status of game on subscribe`() {
        val game = gameService.createGame()
        val messages = mutableListOf<String>()
        stompSession.subscribe(
                "/game/status/${game.id}",
                TestStompFrameHandler(messages))

        await until { messages.count() == 1 }

        assertEquals(game.status.toString(), messages.last())
    }

    @Test
    fun `gets status of game when updated`() {
        var game = gameService.createGame()
        val messages = mutableListOf<String>()

        stompSession.subscribe(
                "/game/status/${game.id}",
                TestStompFrameHandler(messages))

        await until { messages.count() == 1 }

        game = gameService.updateGameStatusForNextPlayer(game, GameStatus.STALEMATE)

        await until { messages.count() == 2 }

        assertEquals(GameStatus.STALEMATE, game.status)
        assertEquals(game.status.toString(), messages.last())
    }
}
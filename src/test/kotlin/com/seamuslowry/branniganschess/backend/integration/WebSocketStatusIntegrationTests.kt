package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompClient
import org.awaitility.kotlin.await
import com.seamuslowry.branniganschess.backend.integration.mocks.GameStompFrameHandler
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompSessionHandler
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.services.GameService
import org.awaitility.kotlin.until
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSession

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketStatusIntegrationTests(
        @LocalServerPort
        val port: Int,
        @Autowired
        private val gameService: GameService,
        @Autowired val stompClient: TestStompClient
) {
    private lateinit var stompSession: StompSession

    @BeforeEach
    fun setUp() {
        val wsUrl = "ws://127.0.0.1:$port/ws"
        stompClient.messageConverter = MappingJackson2MessageConverter()
        stompSession = stompClient.connect(wsUrl, TestStompSessionHandler()).get()
    }

    @AfterEach
    fun tearDown() {
        stompSession.disconnect()
        stompClient.stop()
    }

    @Test
    fun `gets game on subscribe`() {
        val game = gameService.createGame()
        val messages = mutableListOf<Game>()
        stompSession.subscribe(
            "/game/status/${game.id}",
            GameStompFrameHandler(messages))

        await until { messages.count() == 1 }

        assertEquals(game.id, messages.last().id)
    }

    @Test
    fun `gets game when updated`() {
        var game = gameService.createGame()
        val messages = mutableListOf<Game>()

        stompSession.subscribe(
            "/game/status/${game.id}",
            GameStompFrameHandler(messages))

        await until { messages.count() == 1 }

        game = gameService.updateGameStatusForNextPlayer(game, GameStatus.STALEMATE)

        await until { messages.count() == 2 }

        assertEquals(GameStatus.STALEMATE, game.status)
        assertEquals(game.status, messages.last().status)
    }
}
package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompClient
import org.awaitility.kotlin.await
import com.seamuslowry.branniganschess.backend.integration.mocks.MoveStompFrameHandler
import com.seamuslowry.branniganschess.backend.integration.mocks.TestStompSessionHandler
import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.models.Move
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
class WebSocketMovesIntegrationTests(
        @LocalServerPort
        val port: Int,
        @Autowired private val gameService: GameService,
        @Autowired private val stompClient: TestStompClient,
        @Autowired private val testUtils: IntegrationTestUtils
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
    fun `gets shared moves on subscribe`() {
        val game = testUtils.createFullGame()
        gameService.move(game.uuid, MoveRequest(6,0,4,0)) // move white pawn 2
        gameService.move(game.uuid, MoveRequest(1,1,3,1)) // move black pawn 2
        val move = gameService.move(game.uuid, MoveRequest(4,0,3,1)) // take black pawn

        val messages = mutableListOf<Move>()
        stompSession.subscribe(
            "/game/moves/${game.uuid}",
            MoveStompFrameHandler(messages))

        await until { messages.count() == 1 }

        assertEquals(move.id, messages.last().id)
    }

    @Test
    fun `gets no message if no shared moves on subscribe`() {
        val game = testUtils.createFullGame()

        val messages = mutableListOf<Move>()
        stompSession.subscribe(
            "/game/moves/${game.id}",
            MoveStompFrameHandler(messages))

        Thread.sleep(5000) // wait 5 seconds; there's not really a better way to ensure the message is NOT sent

        assertEquals(0, messages.count())
    }

    @Test
    fun `gets a shared move when made`() {
        val game = testUtils.createFullGame()
        val messages = mutableListOf<Move>()

        stompSession.subscribe(
            "/game/moves/${game.uuid}",
            MoveStompFrameHandler(messages))

        gameService.move(game.uuid, MoveRequest(6,0,4,0)) // move white pawn 2
        gameService.move(game.uuid, MoveRequest(1,1,3,1)) // move black pawn 2
        val move = gameService.move(game.uuid, MoveRequest(4,0,3,1)) // take black pawn

        await until { messages.count() == 1 }

        assertEquals(move.id, messages.last().id)
    }
}
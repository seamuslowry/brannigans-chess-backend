package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.MoveService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SearchMovesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameService: GameService,
        @Autowired val moveService: MoveService,
        @Autowired val pieceService: PieceService,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `Finds no moves from a game without colors`() {
        val game = testUtils.createFullGame()
        // move white pawn up one
        gameService.move(game.id, MoveRequest(6,0, 5, 0))
        // move black pawn one
        gameService.move(game.id, MoveRequest(1,0, 2, 0))

        val noMatchGame = testUtils.createFullGame()
        // move white pawn up one
        gameService.move(noMatchGame.id, MoveRequest(6,0, 5, 0))

        mockMvc.get("/moves/${game.id}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(0) }
        }
    }

    @Test
    fun `Finds all moves from a specific game`() {
        val game = testUtils.createFullGame()
        // move white pawn up one
        val whiteMove = gameService.move(game.id, MoveRequest(6,0, 5, 0))
        // move black pawn one
        val blackMove = gameService.move(game.id, MoveRequest(1,0, 2, 0))

        val noMatchGame = testUtils.createFullGame()
        // move white pawn up one
        gameService.move(noMatchGame.id, MoveRequest(6,0, 5, 0))

        mockMvc.get("/moves/${game.id}?color=WHITE&color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[?(@.id == ${whiteMove.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${blackMove.id})]") { isNotEmpty }
        }
    }

    @Test
    fun `Finds moves of a specific color from a game`() {
        val game = testUtils.createFullGame()
        // move white pawn one
        gameService.move(game.id, MoveRequest(6,0, 5, 0))
        // move black pawn one
        val blackMove = gameService.move(game.id, MoveRequest(1,0, 2, 0))

        mockMvc.get("/moves/${game.id}?color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${blackMove.id})]") { isNotEmpty }
        }
    }

    @Test
    fun `Finds any move that took a piece when searching for a specific color move from a game`() {
        val game = testUtils.createFullGame()
        // move white pawn two
        gameService.move(game.id, MoveRequest(6,0, 4, 0))
        // move black pawn two
        val blackMove = gameService.move(game.id, MoveRequest(1,1, 3, 1))
        // take black pawn with white pawn
        val whiteTake = gameService.move(game.id, MoveRequest(4,0, 3, 1))

        mockMvc.get("/moves/${game.id}?color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[?(@.id == ${blackMove.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${whiteTake.id})]") { isNotEmpty }
        }
    }

    @Test
    fun `Finds shared moves for a game`() {
        val game = testUtils.createFullGame()
        // move white pawn two
        gameService.move(game.id, MoveRequest(6,0, 4, 0))
        // move black pawn two
        gameService.move(game.id, MoveRequest(1,1, 3, 1))
        // take black pawn with white pawn
        val whiteTake = gameService.move(game.id, MoveRequest(4,0, 3, 1))

        val result = moveService.findSharedMoves(game.id)

        assertEquals(1, result.count())
        assertEquals(whiteTake.id, result.first().id)
    }

    @Test
    fun `Determines if a piece has moved`() {
        val game = testUtils.createFullGame()
        // move white pawn one
        val whiteMove = gameService.move(game.id, MoveRequest(6,0, 5, 0))

        val pawnMoved = moveService.hasMoved(whiteMove.movingPiece)

        assertTrue(pawnMoved)
    }
}
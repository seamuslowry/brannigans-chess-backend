package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.MoveService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SystemSearchMovesIntegrationTests(
        @Autowired val gameService: GameService,
        @Autowired val moveService: MoveService,
        @Autowired val testUtils: IntegrationTestUtils
) {
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
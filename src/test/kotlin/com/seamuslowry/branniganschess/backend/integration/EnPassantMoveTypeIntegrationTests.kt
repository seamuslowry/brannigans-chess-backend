package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnPassantMoveTypeIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService
) {
    @Test
    fun `will en passant`() {
        val game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid move through the service
        // move white pawn into a position where it could en passant
        pieceService.movePiece(board[6][3]!!, 3,3)

        // move the target pawn in prep for the take
        restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(1,2,3,2),
                Move::class.java
        )

        // take with en passant
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(3,3,2,2),
                Move::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNotNull(response.body?.takenPiece)
    }

    private fun createGame(): Game = gameService.createGame()
}
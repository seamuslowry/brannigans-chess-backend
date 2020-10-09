package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.Piece
import com.seamuslowry.branniganschess.backend.models.PieceStatus
import com.seamuslowry.branniganschess.backend.models.pieces.Queen
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PawnPromotionIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService
) {
    @Test
    fun `will promote a pawn`() {
        val game = gameService.createGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][3]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen
        pieceService.takePiece(board[0][3]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 3)

        // promote
        val response = restTemplate.postForEntity(
                "/pieces/${pawn.id}/promote/QUEEN",
                null,
                Piece::class.java
        )

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pawn.id, savedPawn.id)
        assertTrue(response.body is Queen)
    }
}
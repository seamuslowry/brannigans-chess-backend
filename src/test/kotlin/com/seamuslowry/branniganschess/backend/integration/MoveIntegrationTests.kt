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
class MoveIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService
) {

    @Test
    fun `Throws an exception on an invalid move`() {
        val game = createGame()
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(0,0,0,0),
                String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `moves a piece`() {
        val game = createGame()
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(1,0,2,0),
                Move::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNull(response.body?.takenPiece)
    }

    @Test
    fun `takes a piece`() {
        val game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid move through the service
        pieceService.movePiece(board[1][0]!!, 5,0)

        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(5,0,6,1),
                Move::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNotNull(response.body?.takenPiece)
    }

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
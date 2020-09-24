package com.seamuslowry.branniganschess.backend.branniganschess.integration

import com.seamuslowry.branniganschess.backend.branniganschess.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Move
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import com.seamuslowry.branniganschess.backend.branniganschess.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
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
        pieceService.movePiece(board[0][0]!!, 4,0)

        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(4,0,6,0),
                Move::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNotNull(response.body?.takenPiece)
    }

    private fun createGame(): Game = gameService.createGame()
}
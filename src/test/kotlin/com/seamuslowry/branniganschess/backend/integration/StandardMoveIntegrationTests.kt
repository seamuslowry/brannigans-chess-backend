package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StandardMoveIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository
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
    fun `moves a black piece`() {
        var game = createGame()
        // set it to be black's turn
        gameService.updateGameStatus(game, GameStatus.BLACK_TURN)
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(1,0,2,0),
                Move::class.java
        )

        game = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNull(response.body?.takenPiece)
        assertEquals(GameStatus.WHITE_TURN, game.status)
    }

    @Test
    fun `moves a white piece`() {
        var game = createGame()
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(6,0,5,0),
                Move::class.java
        )

        game = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNull(response.body?.takenPiece)
        assertEquals(GameStatus.BLACK_TURN, game.status)
    }

    @Test
    fun `takes a piece`() {
        val game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid move through the service
        pieceService.movePiece(board[6][0]!!, 2,0)

        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(2,0,1,1),
                Move::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body?.movingPiece)
        assertNotNull(response.body?.takenPiece)
    }

    @Test
    fun `will not let black move off its turn`() {
        val game = createGame()

        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(1,0,2,0),
                String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `will not let white move off its turn`() {
        val game = createGame()

        // white move one
        restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(6,0,5,0),
                Move::class.java
        )

        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(5,0,4,0),
                String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    private fun createGame(): Game = gameService.createGame()
}
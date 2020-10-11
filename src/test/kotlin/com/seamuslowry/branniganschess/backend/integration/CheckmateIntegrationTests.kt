package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.models.pieces.King
import com.seamuslowry.branniganschess.backend.models.pieces.Rook
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
class CheckmateIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `checkmates black`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take every piece that isn't a king or a rook
        pieceService.findAllBy(game.id).filter { !(it is King || it is Rook) }.forEach { pieceService.takePiece(it) }
        // take the two black rooks
        pieceService.takePiece(board[0][0]!!)
        pieceService.takePiece(board[0][7]!!)
        pieceService.movePiece(board[7][0]!!, 1, 0)

        // move other rook to checkmate
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(7,7,0,7),
                Move::class.java
        )

        game = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GameStatus.CHECKMATE, game.status)
    }

    @Test
    fun `checkmates white`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take every piece that isn't a king or a rook
        pieceService.findAllBy(game.id).filter { !(it is King || it is Rook) }.forEach { pieceService.takePiece(it) }
        // take the two white rooks
        pieceService.takePiece(board[7][0]!!)
        pieceService.takePiece(board[7][7]!!)

        // move the rook to block white in
        pieceService.movePiece(board[0][0]!!, 6, 0)

        // set it to be black's turn
        gameService.updateGameStatus(game, GameStatus.BLACK_TURN)

        // move other rook to checkmate
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(0,7,7,7),
                Move::class.java
        )

        game = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GameStatus.CHECKMATE, game.status)
    }

    private fun createGame(): Game = gameService.createGame()
}
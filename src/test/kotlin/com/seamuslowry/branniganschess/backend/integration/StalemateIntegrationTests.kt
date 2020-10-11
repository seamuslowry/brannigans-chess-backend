package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.pieces.Bishop
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
class StalemateIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `stalemates with just the king on one side`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take every piece that isn't a king or a rook
        pieceService.findAllBy(game.id).filter { !(it is King || it is Rook) }.forEach { pieceService.takePiece(it) }
        // take the two black rooks
        pieceService.takePiece(board[0][0]!!)
        pieceService.takePiece(board[0][7]!!)
        // move the black king to the corner
        pieceService.movePiece(board[0][4]!!, 0, 0)
        // move white rook over one col
        pieceService.movePiece(board[7][0]!!, 7, 1)

        // move other rook to stalemate
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(7,7,1,7),
                Move::class.java
        )

        game = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GameStatus.STALEMATE, game.status)
    }

    @Test
    fun `stalemates with more than just the king left on one side`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take every piece that isn't a king or a rook
        pieceService.findAllBy(game.id).filter { !(it is King ||
                (it is Rook && it.color == PieceColor.WHITE) ||
                (it is Bishop && it.color == PieceColor.BLACK)) }.
        forEach { pieceService.takePiece(it) }
        // take the queen side white rook
        pieceService.takePiece(board[7][0]!!)
        // take the queen side black bishop
        pieceService.takePiece(board[0][2]!!)
        // move the black king to the corner
        pieceService.movePiece(board[0][4]!!, 0, 0)
        // move the black bishop next to the king
        pieceService.movePiece(board[0][5]!!, 0, 1)
        // move the white king to block moving up the board
        pieceService.movePiece(board[7][4]!!, 2, 1)

        // move white rook to stalemate
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(7,7,0,7),
                Move::class.java
        )

        game = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(GameStatus.STALEMATE, game.status)
    }

    private fun createGame(): Game = gameService.createGame()
}
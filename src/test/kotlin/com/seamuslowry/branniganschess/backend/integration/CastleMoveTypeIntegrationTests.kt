package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.models.MoveType
import com.seamuslowry.branniganschess.backend.models.pieces.King
import com.seamuslowry.branniganschess.backend.models.pieces.Rook
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CastleMoveTypeIntegrationTests (
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService
) {
    @Test
    fun `king-side castles - WHITE`() {
        val game = gameService.createGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take white king-side bishop
        pieceService.takePiece(board[7][5]!!)
        // take white king-side knight
        pieceService.takePiece(board[7][6]!!)

        // castle
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(7,4,7,6),
                Move::class.java
        )

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotNull(response.body?.movingPiece)
        Assertions.assertNull(response.body?.takenPiece)
        Assertions.assertEquals(MoveType.KING_SIDE_CASTLE, response.body?.moveType)
        Assertions.assertTrue(board[7][5] is Rook)
        Assertions.assertTrue(board[7][6] is King)
    }

    @Test
    fun `queen-side castles - WHITE`() {
        val game = gameService.createGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take white queen-side bishop
        pieceService.takePiece(board[7][2]!!)
        // take white queen-side knight
        pieceService.takePiece(board[7][1]!!)
        // take white queen
        pieceService.takePiece(board[7][3]!!)

        // castle
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(7,4,7,2),
                Move::class.java
        )

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotNull(response.body?.movingPiece)
        Assertions.assertNull(response.body?.takenPiece)
        Assertions.assertEquals(MoveType.QUEEN_SIDE_CASTLE, response.body?.moveType)
        Assertions.assertTrue(board[7][3] is Rook)
        Assertions.assertTrue(board[7][2] is King)
    }

    @Test
    fun `king-side castles - BLACK`() {
        val game = gameService.createGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take black king-side bishop
        pieceService.takePiece(board[0][5]!!)
        // take black king-side knight
        pieceService.takePiece(board[0][6]!!)

        // set it to be black's turn
        gameService.updateGameStatus(game, GameStatus.BLACK_TURN)

        // castle
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(0,4,0,6),
                Move::class.java
        )

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotNull(response.body?.movingPiece)
        Assertions.assertNull(response.body?.takenPiece)
        Assertions.assertEquals(MoveType.KING_SIDE_CASTLE, response.body?.moveType)
        Assertions.assertTrue(board[0][5] is Rook)
        Assertions.assertTrue(board[0][6] is King)
    }

    @Test
    fun `queen-side castles - BLACK`() {
        val game = gameService.createGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take black queen-side bishop
        pieceService.takePiece(board[0][2]!!)
        // take black queen-side knight
        pieceService.takePiece(board[0][1]!!)
        // take black queen
        pieceService.takePiece(board[0][3]!!)

        // set it to be black's turn
        gameService.updateGameStatus(game, GameStatus.BLACK_TURN)

        // castle
        val response = restTemplate.postForEntity(
                "/moves/${game.id}",
                MoveRequest(0,4,0,2),
                Move::class.java
        )

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertNotNull(response.body?.movingPiece)
        Assertions.assertNull(response.body?.takenPiece)
        Assertions.assertEquals(MoveType.QUEEN_SIDE_CASTLE, response.body?.moveType)
        Assertions.assertTrue(board[0][3] is Rook)
        Assertions.assertTrue(board[0][2] is King)
    }
}
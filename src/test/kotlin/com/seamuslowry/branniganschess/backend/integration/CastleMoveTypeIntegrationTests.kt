package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.MoveType
import com.seamuslowry.branniganschess.backend.models.pieces.King
import com.seamuslowry.branniganschess.backend.models.pieces.Rook
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CastleMoveTypeIntegrationTests (
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `king-side castles - WHITE`() {
        val game = testUtils.createFullGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take white king-side bishop
        pieceService.takePiece(board[7][5]!!)
        // take white king-side knight
        pieceService.takePiece(board[7][6]!!)

        // castle
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(7,4,7,6))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
            jsonPath("moveType") { value(MoveType.KING_SIDE_CASTLE.toString()) }
        }

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertTrue(board[7][5] is Rook)
        Assertions.assertTrue(board[7][6] is King)
    }

    @Test
    fun `queen-side castles - WHITE`() {
        val game = testUtils.createFullGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take white queen-side bishop
        pieceService.takePiece(board[7][2]!!)
        // take white queen-side knight
        pieceService.takePiece(board[7][1]!!)
        // take white queen
        pieceService.takePiece(board[7][3]!!)

        // castle
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(7,4,7,2))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
            jsonPath("moveType") { value(MoveType.QUEEN_SIDE_CASTLE.toString()) }
        }

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertTrue(board[7][3] is Rook)
        Assertions.assertTrue(board[7][2] is King)
    }

    @Test
    fun `king-side castles - BLACK`() {
        val game = testUtils.createFullGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take black king-side bishop
        pieceService.takePiece(board[0][5]!!)
        // take black king-side knight
        pieceService.takePiece(board[0][6]!!)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)

        // castle
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(0,4,0,6))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
            jsonPath("moveType") { value(MoveType.KING_SIDE_CASTLE.toString()) }
        }

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertTrue(board[0][5] is Rook)
        Assertions.assertTrue(board[0][6] is King)
    }

    @Test
    fun `queen-side castles - BLACK`() {
        val game = testUtils.createFullGame()
        var board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take black queen-side bishop
        pieceService.takePiece(board[0][2]!!)
        // take black queen-side knight
        pieceService.takePiece(board[0][1]!!)
        // take black queen
        pieceService.takePiece(board[0][3]!!)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)

        // castle
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(0,4,0,2))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
            jsonPath("moveType") { value(MoveType.QUEEN_SIDE_CASTLE.toString()) }
        }

        board = pieceService.getPiecesAsBoard(game.id)

        Assertions.assertTrue(board[0][3] is Rook)
        Assertions.assertTrue(board[0][2] is King)
    }
}
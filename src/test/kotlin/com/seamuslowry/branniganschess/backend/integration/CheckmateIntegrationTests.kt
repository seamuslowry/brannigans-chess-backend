package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.pieces.King
import com.seamuslowry.branniganschess.backend.models.pieces.Rook
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
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
class CheckmateIntegrationTests(
        @Autowired val mockMvc: MockMvc,
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
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(7,7,0,7))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
        }

        game = gameRepository.getOne(game.id)

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
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)

        // move other rook to checkmate
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(0,7,7,7))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.CHECKMATE, game.status)
    }

    private fun createGame(): Game = gameService.createGame()
}
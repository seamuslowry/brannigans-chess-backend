package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
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
class EnPassantMoveTypeIntegrationTests(
        @Autowired val mockMvc: MockMvc,
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

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)

        // move the target pawn in prep for the take
        mockMvc.post("/moves/${game.uuid}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(1,2,3,2))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
        }

        // take with en passant
        mockMvc.post("/moves/${game.uuid}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(3,3,2,2))
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isNotEmpty }
        }
    }

    private fun createGame(): Game = gameService.createGame()
}
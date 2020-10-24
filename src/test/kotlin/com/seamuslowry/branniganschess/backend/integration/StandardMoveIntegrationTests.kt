package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StandardMoveIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `Throws an exception on an invalid move`() {
        val game = createGame()
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(0,0,0,0))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isBadRequest }
        }
    }

    @Test
    fun `moves a black piece`() {
        var game = createGame()
        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(1,0,2,0))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.WHITE_TURN, game.status)
    }

    @Test
    fun `moves a white piece`() {
        var game = createGame()
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(6,0,5,0))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.BLACK_TURN, game.status)
    }

    @Test
    fun `takes a piece`() {
        val game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid move through the service
        pieceService.movePiece(board[6][0]!!, 2,0)

        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(2,0,1,1))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isNotEmpty }
        }
    }

    @Test
    fun `will not let black move off its turn`() {
        val game = createGame()

        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(1,0,2,0))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isBadRequest }
        }
    }

    @Test
    fun `will not let white move off its turn`() {
        val game = createGame()

        // white move one
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(6,0,5,0))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
        }
//        restTemplate.postForEntity(
//                "/moves/${game.id}",
//                MoveRequest(6,0,5,0),
//                Move::class.java
//        )

        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(5,0,4,0))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isBadRequest }
        }
//        val response = restTemplate.postForEntity(
//                "/moves/${game.id}",
//                MoveRequest(5,0,4,0),
//                String::class.java
//        )
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    private fun createGame(): Game = gameService.createGame()
}
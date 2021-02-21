package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AllGameDataIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `retrieves all the current game data - SPECTATOR`() {
        val game = testUtils.createFullGame()

        // white pawn up two
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(6,0,4,0))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
        }

        // black pawn up two
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(1,0,3,0))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", game.blackPlayer?.authId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.get("/games/${game.id}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.game.id") { value(game.id) }
            jsonPath("$.pieces.length()") { value(32) }
            jsonPath("$.moves.length()") { value(2) }
        }
    }

    @Test
    fun `attempts to get the current game data - WHITE`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/games/${game.id}") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `attempts to get the current game data - BLACK`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/games/${game.id}") {
            with(jwt().jwt { it.claim("sub", game.blackPlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }
}
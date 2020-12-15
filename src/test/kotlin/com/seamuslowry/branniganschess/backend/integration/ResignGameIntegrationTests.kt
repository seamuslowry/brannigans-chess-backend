package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PlayerService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ResignGameIntegrationTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val playerService: PlayerService,
    @Autowired val gameService: GameService,
    @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `white can resign a game`() {
        val game = testUtils.createFullGame()

        mockMvc.post("/players/resign/${game.id}") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("status") { value(GameStatus.BLACK_VICTORY.toString()) }
        }
    }

    @Test
    fun `black can resign a game`() {
        val game = testUtils.createFullGame()

        mockMvc.post("/players/resign/${game.id}") {
            with(jwt().jwt { it.claim("sub", game.blackPlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("status") { value(GameStatus.WHITE_VICTORY.toString()) }
        }
    }

    @Test
    fun `unrelated player cannot resign a game`() {
        val game = testUtils.createFullGame()
        val player = playerService.authenticatePlayer("cannot-resign-player")

        mockMvc.post("/players/resign/${game.id}") {
            with(jwt().jwt { it.claim("sub", player.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("status") { value(GameStatus.WHITE_TURN.toString()) }
        }
    }
}
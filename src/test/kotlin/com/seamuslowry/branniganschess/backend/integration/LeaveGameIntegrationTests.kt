package com.seamuslowry.branniganschess.backend.integration

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
class LeaveGameIntegrationTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val playerService: PlayerService,
    @Autowired val gameService: GameService
) {
    @Test
    fun `can leave a game with one player`() {
        val playerAuthId = "leave-game-itest"
        val player = playerService.authenticatePlayer(playerAuthId)
        val game = gameService.createGame()

        mockMvc.post("/players/join/${game.uuid}") {
            with(jwt().jwt { it.claim("sub", player.authId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.post("/players/leave/${game.id}") {
            with(jwt().jwt { it.claim("sub", player.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("status") { value(GameStatus.WAITING_FOR_PLAYERS.toString()) }
        }
    }

    @Test
    fun `cannot leave a game with two players`() {
        val playerOneAuthId = "cannot-leave-itest-one"
        val playerTwoAuthId = "cannot-leave-itest-two"
        val playerOne = playerService.authenticatePlayer(playerOneAuthId)
        val playerTwo = playerService.authenticatePlayer(playerTwoAuthId)
        val game = gameService.createGame()

        mockMvc.post("/players/join/${game.uuid}") {
            with(jwt().jwt { it.claim("sub", playerOne.authId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.post("/players/join/${game.uuid}") {
            with(jwt().jwt { it.claim("sub", playerTwo.authId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.post("/players/leave/${game.id}") {
            with(jwt().jwt { it.claim("sub", playerOne.authId) })
        }.andExpect {
            status { isConflict }
        }

        mockMvc.post("/players/leave/${game.id}") {
            with(jwt().jwt { it.claim("sub", playerTwo.authId) })
        }.andExpect {
            status { isConflict }
        }
    }
}
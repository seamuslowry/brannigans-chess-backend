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
class JoinGameIntegrationTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val playerService: PlayerService,
    @Autowired val gameService: GameService
) {
    @Test
    fun `will join a new game`() {
        val playerAuthId = "join-game-as-white-itest"
        val player = playerService.authenticatePlayer(playerAuthId)
        val game = gameService.createGame()

        mockMvc.post("/players/join/${game.uuid}") {
            with(jwt().jwt { it.claim("sub", player.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("whitePlayer") { isNotEmpty }
            jsonPath("blackPlayer") { isEmpty }
            jsonPath("status") { value(GameStatus.WAITING_FOR_BLACK.toString()) }
            jsonPath("whitePlayer.authId") { value(playerAuthId) }
        }
    }

    @Test
    fun `will join a new game as black`() {
        val playerAuthId = "join-game-as-black-itest"
        val player = playerService.authenticatePlayer(playerAuthId)
        val game = gameService.createGame()

        mockMvc.post("/players/join/${game.uuid}?color=BLACK") {
            with(jwt().jwt { it.claim("sub", player.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("whitePlayer") { isEmpty }
            jsonPath("blackPlayer") { isNotEmpty }
            jsonPath("status") { value(GameStatus.WAITING_FOR_WHITE.toString()) }
            jsonPath("blackPlayer.authId") { value(playerAuthId) }
        }
    }

    @Test
    fun `filling a game starts play`() {
        val blackPlayerAuthId = "black-fill-game-itest"
        val whitePlayerAuthId = "white-fill-game-itest"
        val blackPlayer = playerService.authenticatePlayer(blackPlayerAuthId)
        val whitePlayer = playerService.authenticatePlayer(whitePlayerAuthId)
        val game = gameService.createGame()

        mockMvc.post("/players/join/${game.uuid}") {
            with(jwt().jwt { it.claim("sub", whitePlayer.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("status") { value(GameStatus.WAITING_FOR_BLACK.toString()) }
        }

        mockMvc.post("/players/join/${game.uuid}") {
            with(jwt().jwt { it.claim("sub", blackPlayer.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("status") { value(GameStatus.WHITE_TURN.toString()) }
        }
    }
}
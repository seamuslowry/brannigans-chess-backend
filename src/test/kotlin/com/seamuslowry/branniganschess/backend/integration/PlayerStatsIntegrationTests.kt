package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.utils.Constants
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PlayerStatsIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val playerRepository: PlayerRepository,
        @Autowired val gameRepository: GameRepository,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `Gets a player's stat information`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))

        val whiteAllStatusGames = testUtils.getGamesInAllStatuses(whitePlayer = playerOne)
        val blackAllStatusGames = testUtils.getGamesInAllStatuses(blackPlayer = playerOne)

        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        gameRepository.save(Game("1 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/stats/${playerOne.id}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("whiteGames") { value(whiteAllStatusGames.count()) }
            jsonPath("blackGames") { value(blackAllStatusGames.count()) }
            jsonPath("whiteWins") { value(1) }
            jsonPath("blackWins") { value(1) }
            jsonPath("whiteDraws") { value(1) }
            jsonPath("blackDraws") { value(1) }
        }
    }
}
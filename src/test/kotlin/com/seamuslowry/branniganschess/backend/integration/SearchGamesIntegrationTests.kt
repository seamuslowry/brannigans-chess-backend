package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SearchGamesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val playerRepository: PlayerRepository,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `Finds active games`() {
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        val wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player(System.nanoTime().toString()))
        wonGame.winner = winner
        gameRepository.save(wonGame)

        mockMvc.get("/games?active=true&size=${gameRepository.count()}")
            .andExpect {
                status { isOk }
                jsonPath("content[?(@.id == ${activeGame.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${wonGame.id})]") { isEmpty }
            }
    }

    @Test
    fun `Finds won games`() {
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        var wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player(System.nanoTime().toString()))
        wonGame.winner = winner
        wonGame = gameRepository.save(wonGame)

        mockMvc.get("/games?active=false&size=${gameRepository.count()}")
            .andExpect {
                status { isOk }
                jsonPath("content[?(@.id == ${activeGame.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${wonGame.id})]") { isNotEmpty }
            }
    }

    @Test
    fun `Finds all games`() {
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        val wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player(System.nanoTime().toString()))
        wonGame.winner = winner
        gameRepository.save(wonGame)

        mockMvc.get("/games?size=${gameRepository.count()}")
            .andExpect {
                status { isOk }
                jsonPath("content[?(@.id == ${activeGame.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${wonGame.id})]") { isNotEmpty }
            }
    }
}
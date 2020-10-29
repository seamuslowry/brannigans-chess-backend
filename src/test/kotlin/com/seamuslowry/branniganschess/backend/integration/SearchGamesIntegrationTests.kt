package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
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

    @Test
    fun `Finds all a player's games`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))
        val whiteActiveGame = gameRepository.save(Game("1 - Player White Active Game", whitePlayer = playerOne))
        val blackActiveGame = gameRepository.save(Game("1 - Player Black Active Game", blackPlayer = playerOne))
        val whiteInactiveGame = gameRepository.save(Game("1 - Player White Inactive Game", whitePlayer = playerOne, winner = playerOne))
        val blackInactiveGame = gameRepository.save(Game("1 - Player Black Inactive Game", blackPlayer = playerOne, winner = playerOne))
        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("1 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(4) }
            jsonPath("$[?(@.id == ${whiteActiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${blackActiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${blackInactiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${whiteInactiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's active games`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))
        val whiteActiveGame = gameRepository.save(Game("2 - Player White Active Game", whitePlayer = playerOne))
        val blackActiveGame = gameRepository.save(Game("2 - Player Black Active Game", blackPlayer = playerOne))
        val whiteInactiveGame = gameRepository.save(Game("2 - Player White Inactive Game", whitePlayer = playerOne, winner = playerOne))
        val blackInactiveGame = gameRepository.save(Game("2 - Player Black Inactive Game", blackPlayer = playerOne, winner = playerOne))
        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("2 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games?active=true") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[?(@.id == ${whiteActiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${blackActiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${blackInactiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${whiteInactiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's inactive games`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))
        val whiteActiveGame = gameRepository.save(Game("2 - Player White Active Game", whitePlayer = playerOne))
        val blackActiveGame = gameRepository.save(Game("2 - Player Black Active Game", blackPlayer = playerOne))
        val whiteInactiveGame = gameRepository.save(Game("2 - Player White Inactive Game", whitePlayer = playerOne, winner = playerOne))
        val blackInactiveGame = gameRepository.save(Game("2 - Player Black Inactive Game", blackPlayer = playerOne, winner = playerOne))
        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("2 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games?active=false") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[?(@.id == ${whiteActiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${blackActiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${blackInactiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${whiteInactiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's color active games`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))
        val whiteActiveGame = gameRepository.save(Game("2 - Player White Active Game", whitePlayer = playerOne))
        val blackActiveGame = gameRepository.save(Game("2 - Player Black Active Game", blackPlayer = playerOne))
        val whiteInactiveGame = gameRepository.save(Game("2 - Player White Inactive Game", whitePlayer = playerOne, winner = playerOne))
        val blackInactiveGame = gameRepository.save(Game("2 - Player Black Inactive Game", blackPlayer = playerOne, winner = playerOne))
        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("2 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games?active=true&color=BLACK") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${whiteActiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${blackActiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${blackInactiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${whiteInactiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's color inactive games`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))
        val whiteActiveGame = gameRepository.save(Game("2 - Player White Active Game", whitePlayer = playerOne))
        val blackActiveGame = gameRepository.save(Game("2 - Player Black Active Game", blackPlayer = playerOne))
        val whiteInactiveGame = gameRepository.save(Game("2 - Player White Inactive Game", whitePlayer = playerOne, winner = playerOne))
        val blackInactiveGame = gameRepository.save(Game("2 - Player Black Inactive Game", blackPlayer = playerOne, winner = playerOne))
        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("2 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games?active=false&color=BLACK") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${whiteActiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${blackActiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${blackInactiveGame.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${whiteInactiveGame.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }
}
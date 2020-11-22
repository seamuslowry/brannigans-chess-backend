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
class SearchGamesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val playerRepository: PlayerRepository,
        @Autowired val gameRepository: GameRepository,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `Finds games of each status`() {
        val allStatusGames = testUtils.getGamesInAllStatuses()

        allStatusGames.forEach {
            mockMvc.get("/games?status=${it.key}&size=${gameRepository.count()}")
                .andExpect {
                    status { isOk }
                    jsonPath("content[?(@.id == ${it.value.id})]") { isNotEmpty }
                }
        }
    }

    @Test
    fun `Finds open games`() {
        val allStatusGames = testUtils.getGamesInAllStatuses()

        mockMvc.get("/games?${Constants.openStatuses.joinToString(separator = "", prefix = "status=") { "&status=${it}" }}&size=${gameRepository.count()}")
            .andExpect {
                status { isOk }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_TURN]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_TURN]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            }
    }

    @Test
    fun `Finds active games`() {
        val allStatusGames = testUtils.getGamesInAllStatuses()

        mockMvc.get("/games?${Constants.activeStatuses.joinToString(separator = "", prefix = "status=") { "&status=${it}" }}&size=${gameRepository.count()}")
            .andExpect {
                status { isOk }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            }
    }

    @Test
    fun `Finds won games`() {
        val allStatusGames = testUtils.getGamesInAllStatuses()

        mockMvc.get("/games?${Constants.inactiveStatuses.joinToString(separator = "", prefix = "status=") { "&status=${it}" }}&size=${gameRepository.count()}")
            .andExpect {
                status { isOk }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_TURN]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_TURN]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isNotEmpty }
                jsonPath("content[?(@.id == ${allStatusGames[GameStatus.STALEMATE]?.id})]") { isNotEmpty }
            }
    }

    @Test
    fun `Finds all a player's games`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))

        val whiteAllStatusGames = testUtils.getGamesInAllStatuses(whitePlayer = playerOne)
        val blackAllStatusGames = testUtils.getGamesInAllStatuses(blackPlayer = playerOne)

        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("1 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games/${playerAuthId}?size=${gameRepository.count()}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.content.length()") { value(whiteAllStatusGames.count() + blackAllStatusGames.count()) }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.STALEMATE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.STALEMATE]?.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's games by status`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))

        val whiteAllStatusGames = testUtils.getGamesInAllStatuses(whitePlayer = playerOne)
        val blackAllStatusGames = testUtils.getGamesInAllStatuses(blackPlayer = playerOne)

        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("1 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games/${playerAuthId}?${Constants.activeStatuses.joinToString(separator = "", prefix = "status=") { "&status=${it}" }}&size=${gameRepository.count()}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.content.length()") { value(Constants.activeStatuses.count() * 2) }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's white games by status`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))

        val whiteAllStatusGames = testUtils.getGamesInAllStatuses(whitePlayer = playerOne)
        val blackAllStatusGames = testUtils.getGamesInAllStatuses(blackPlayer = playerOne)

        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("1 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games/${playerAuthId}?${Constants.activeStatuses.joinToString(separator = "", prefix = "status=") { "&status=${it}" }}&color=WHITE&size=${gameRepository.count()}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.content.length()") { value(Constants.activeStatuses.count()) }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds all a player's black games by status`() {
        val playerAuthId = System.nanoTime().toString()
        val playerOne = playerRepository.save(Player(playerAuthId))

        val whiteAllStatusGames = testUtils.getGamesInAllStatuses(whitePlayer = playerOne)
        val blackAllStatusGames = testUtils.getGamesInAllStatuses(blackPlayer = playerOne)

        val playerTwo = playerRepository.save(Player(System.nanoTime().toString()))
        val noMatchGame = gameRepository.save(Game("1 - Player Game Search No Match", whitePlayer = playerTwo))

        mockMvc.get("/players/games/${playerAuthId}?${Constants.activeStatuses.joinToString(separator = "", prefix = "status=") { "&status=${it}" }}&color=BLACK&size=${gameRepository.count()}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.content.length()") { value(Constants.activeStatuses.count()) }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${whiteAllStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_BLACK]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_WHITE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WAITING_FOR_PLAYERS]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_TURN]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECK]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_PROMOTION]?.id})]") { isNotEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.WHITE_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.BLACK_CHECKMATE]?.id})]") { isEmpty }
            jsonPath("content[?(@.id == ${blackAllStatusGames[GameStatus.STALEMATE]?.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${noMatchGame.id})]") { isEmpty }
        }
    }
}
package com.seamuslowry.branniganschess.backend.integration.utils

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Player
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PlayerService
import org.springframework.stereotype.Component

@Component
class IntegrationTestUtils(
    private val gameService: GameService,
    private val gameRepository: GameRepository,
    private val playerService: PlayerService
) {
    fun createFullGame(): Game {
        var game = gameService.createGame()
        val playerOne =  playerService.authenticatePlayer("util-game-one-${System.nanoTime()}")
        val playerTwo =  playerService.authenticatePlayer("util-game-two-${System.nanoTime()}")
        game = playerService.joinGame(game.uuid, playerOne.authId, PieceColor.WHITE)
        game = playerService.joinGame(game.uuid, playerTwo.authId, PieceColor.BLACK)

        return game
    }

    fun getGamesInAllStatuses(
        whitePlayer: Player? = null,
        blackPlayer: Player? = null
    ): Map<GameStatus, Game> =
        GameStatus.values()
            .map { it to gameRepository.save(Game("${System.nanoTime()}-${it}",
                                                  whitePlayer,
                                                  blackPlayer,
                                                  status = it)) }
            .toMap()
}
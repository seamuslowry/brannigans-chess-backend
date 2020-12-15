package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.dtos.AdditionalPlayerInfo
import com.seamuslowry.branniganschess.backend.dtos.PlayerStatInfo
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.utils.Constants
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class PlayerService (
        private val playerRepository: PlayerRepository,
        private val gameService: GameService
) {
    /**
     * Process authentication information for a player with the provided authentication id.
     * If there is no player with that id, create one and return it.
     *
     * @param authId the authentication id
     * @param additionalPlayerInfo additional information about the player
     *
     * @return a player with that authentication id and information
     */
    fun authenticatePlayer(authId: String, additionalPlayerInfo: AdditionalPlayerInfo = AdditionalPlayerInfo()): Player =
        playerRepository.findOne(withAuthId(authId)).orElseGet {
            playerRepository.save(
                Player(authId, name = additionalPlayerInfo.name, imageUrl = additionalPlayerInfo.imageUrl)
            )
        }

    /**
     * Change a player's display name
     *
     * @param authId the player's authentication id
     * @param newName the new name
     *
     * @return the updated player
     */
    @PreAuthorize("authentication.name == #authId")
    fun changeName(authId: String, newName: String): Player {
        val player = getByAuthId(authId)

        player.name = newName

        return playerRepository.save(player)
    }

    /**
     * Get the stat information for the provided player
     *
     * @param playerId the player's id
     *
     * @return the stat information for that player
     */
    fun getStats(playerId: Long): PlayerStatInfo {
        val player = getById(playerId)

        return PlayerStatInfo(
            gameService.countPlayerGames(player, PieceColor.WHITE, Constants.allStatuses), // all white games
            gameService.countPlayerGames(player, PieceColor.BLACK, Constants.allStatuses), // all black games
            gameService.countPlayerGames(player, PieceColor.WHITE, listOf(GameStatus.WHITE_VICTORY)), // all white wins
            gameService.countPlayerGames(player, PieceColor.BLACK, listOf(GameStatus.BLACK_VICTORY)), // all black wins
            gameService.countPlayerGames(player, PieceColor.WHITE, listOf(GameStatus.STALEMATE)), // all white stalemates
            gameService.countPlayerGames(player, PieceColor.BLACK, listOf(GameStatus.STALEMATE)) // all black stalemates
        )
    }

    /**
     * Get all the games that meet the passed criteria for the player with the provided auth id.
     *
     * @param playerId the id of the player
     * @param color the color the player must be in the games
     * @param statuses matching games must have a status present in this list; defaults to all statuses
     * @param pageable the pageable descriptor
     *
     * @return the [Page] of games
     */
    fun getGames(
        playerId: Long,
        color: PieceColor?,
        statuses: Iterable<GameStatus> = Constants.allStatuses,
        pageable: Pageable
    ): Page<Game> {
        val player = getById(playerId)
        return getGames(player, color, statuses, pageable)
    }

    /**
     * Has a player join a game.
     *
     * @param gameId the id of the game to join
     * @param authId the auth id of the player that will join
     * @param color option color to join as; will pick an available color if not provided
     *
     * @return the updated game
     */
    fun joinGame(gameId: Long, authId: String, color: PieceColor?): Game {
        val player = getByAuthId(authId)
        return gameService.addPlayer(gameId, player, color)
    }

    /**
     * Has a player leave a game.
     *
     * @param gameId the id of the game to leave
     * @param authId the auth id of the player that will join
     *
     * @return the updated game
     */
    fun leaveGame(gameId: Long, authId: String): Game {
        val player = getByAuthId(authId)
        return gameService.removePlayer(gameId, player)
    }

    /**
     * Has a player resign a game.
     *
     * @param gameId the id of the game to join
     * @param authId the auth id of the player that will join
     *
     * @return the updated game
     */
    fun resignGame(gameId: Long, authId: String): Game {
        val player = getByAuthId(authId)
        return gameService.resignPlayer(gameId, player)
    }

    private fun getByAuthId(authId: String): Player =
        playerRepository.findOne(Specification.where(withAuthId(authId))).orElse(null)
            ?: throw EntityNotFoundException("No player with that authorization ID")

    private fun getById(id: Long): Player =
        playerRepository.findById(id).orElse(null)
            ?: throw EntityNotFoundException("No player with that authorization ID")

    private fun getGames(player: Player, color: PieceColor?, statuses: Iterable<GameStatus>, pageable: Pageable) = gameService.findPlayerGames(player, color, statuses, pageable)

    private fun withAuthId(authId: String): Specification<Player> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Player>("authId"), authId)
    }
}
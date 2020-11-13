package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.dtos.AdditionalPlayerInfo
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.utils.Constants
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
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
     * Will update the player to use new additional information.
     *
     * @param authId the authentication id
     * @param additionalPlayerInfo additional information about the player
     *
     * @return a player with that authentication id and information
     */
    fun authenticatePlayer(authId: String, additionalPlayerInfo: AdditionalPlayerInfo = AdditionalPlayerInfo()): Player {
        val player = playerRepository.findOne(withAuthId(authId)).orElse(Player(authId))

        player.name = additionalPlayerInfo.name
        player.imageUrl = additionalPlayerInfo.imageUrl

        return playerRepository.save(player)
    }

    /**
     * Get all the games that meet the passed criteria for the player with the provided auth id.
     *
     * @param authId the auth id of the player
     * @param color the color the player must be in the games
     * @param statuses matching games must have a status present in this list; defaults to all statuses
     * @param pageable the pageable descriptor
     *
     * @return the list of games
     */
    fun getGames(
        authId: String,
        color: PieceColor?,
        statuses: Iterable<GameStatus> = Constants.allStatuses,
        pageable: Pageable
    ): Page<Game> {
        val player = getByAuthId(authId)
        return getGames(player, color, statuses, pageable)
    }

    fun joinGame(gameId: Long, authId: String, color: PieceColor?): Game {
        val player = getByAuthId(authId)
        return gameService.addPlayer(gameId, player, color)
    }

    fun leaveGame(gameId: Long, authId: String): Game {
        val player = getByAuthId(authId)
        return gameService.removePlayer(gameId, player)
    }

    private fun getByAuthId(authId: String): Player =
        playerRepository.findOne(Specification.where(withAuthId(authId))).orElse(null)
            ?: throw EntityNotFoundException("No player with that authorization ID")

    private fun getGames(player: Player, color: PieceColor?, statuses: Iterable<GameStatus>, pageable: Pageable) = gameService.findPlayerGames(player, color, statuses, pageable)

    private fun withAuthId(authId: String): Specification<Player> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Player>("authId"), authId)
    }
}
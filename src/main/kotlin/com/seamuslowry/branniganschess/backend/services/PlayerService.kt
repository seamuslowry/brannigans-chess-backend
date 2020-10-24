package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class PlayerService (
        private val playerRepository: PlayerRepository,
        private val gameService: GameService
) {
    /**
     * Given an google ID, get or create the player.
     *
     * @param googleId the google ID to search for
     * @return the found or created player
     */
    fun getOrCreate(googleId: String): Player {
        var player: Player?

        try {
            player = getByAuthId(googleId)
            player = player ?: playerRepository.save(Player(googleId))
        } catch (e: Exception) {
            player = getByAuthId(googleId)
        }

        return player ?: throw NoSuchElementException("Player cannot be found or created.")
    }

    /**
     * Get all the games that meet the passed criteria for the player with the provided auth id.
     *
     * @param authId the auth id of the player
     * @param color the color the player must be in the games
     * @param active the status of the games
     *
     * @return the list of games
     */
    fun getGames(authId: String, color: PieceColor?, active: Boolean?): Iterable<Game> {
        val player = getByAuthId(authId) ?: throw EntityNotFoundException("No player with that authorization ID")
        return getGames(player, color, active)
    }

    private fun getGames(player: Player, color: PieceColor?, active: Boolean?) = gameService.findPlayerGames(player, color, active)

    private fun getByAuthId(authId: String): Player? = playerRepository.findOne(Specification.where(withAuthId(authId))).orElse(null)

    private fun withAuthId(authId: String): Specification<Player> = Specification {
        root,
        _,
        // cannot use authId as that does not actually exist on the table
        criteriaBuilder -> criteriaBuilder.equal(root.get<Player>("googleId"), authId)
    }
}
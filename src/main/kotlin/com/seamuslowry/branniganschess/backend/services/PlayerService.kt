package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.dtos.SignupException
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
     * Given an google ID, create a player.
     *
     * @param googleId the google ID to create the player
     * @return the created player
     */
    fun googleSignup(googleId: String): Player {
        if (playerRepository.findOne(withGoogleId(googleId)).isPresent) throw SignupException("Already signed up with google")

        return playerRepository.save(Player(googleId = googleId))
    }

    /**
     * Given an google ID, ensure that a player exists.
     *
     * @param googleId the google ID to check for
     * @return the found player
     */
    fun googleLogin(googleId: String): Player {
        val player = playerRepository.findOne(withGoogleId(googleId))
        if (player.isEmpty) throw SignupException("Not signed up with google")

        return player.get()
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

    private fun getByAuthId(authId: String): Player? = playerRepository.findOne(Specification.where(withAuthId(authId))).orElse(null)

    private fun getGames(player: Player, color: PieceColor?, active: Boolean?) = gameService.findPlayerGames(player, color, active)

    private fun withAuthId(authId: String): Specification<Player> = Specification.where(withGoogleId(authId))!!

    private fun withGoogleId(googleId: String): Specification<Player> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Player>("googleId"), googleId)
    }
}
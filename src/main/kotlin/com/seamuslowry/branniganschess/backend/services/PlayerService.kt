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
     * @param googleId the google id of the player
     * @param color the color the player must be in the games
     * @param active the status of the games
     *
     * @return the list of games
     */
    fun getGames(googleId: String, color: PieceColor?, active: Boolean?): Iterable<Game> {
        val player = getByGoogleId(googleId) ?: throw EntityNotFoundException("No player with that authorization ID")
        return getGames(player, color, active)
    }

    private fun getByGoogleId(googleId: String): Player? = playerRepository.findOne(Specification.where(withGoogleId(googleId))).orElse(null)

    private fun getGames(player: Player, color: PieceColor?, active: Boolean?) = gameService.findPlayerGames(player, color, active)

    private fun withGoogleId(googleId: String): Specification<Player> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Player>("googleId"), googleId)
    }
}
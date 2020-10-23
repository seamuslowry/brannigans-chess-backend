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
    // TODO itest on this
    fun getOrCreate(authId: String): Player {
        var player: Player?

        try {
            player = getByAuthId(authId)
            player = player ?: playerRepository.save(Player(authId))
        } catch (e: Exception) {
            player = getByAuthId(authId)
        }

        return player ?: throw NoSuchElementException("Player cannot be found or created.")
    }

    fun getGames(authId: String, color: PieceColor?, active: Boolean?): Iterable<Game> {
        val player = getByAuthId(authId) ?: throw EntityNotFoundException("No player with that authorization ID")
        return getGames(player, color, active)
    }

    private fun getGames(player: Player, color: PieceColor?, active: Boolean?) = gameService.findPlayerGames(player, color, active)

    private fun getByAuthId(authId: String): Player? = playerRepository.findOne(Specification.where(withAuthId(authId))).orElse(null)

    private fun withAuthId(authId: String): Specification<Player> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Player>("authId"), authId)
    }
}
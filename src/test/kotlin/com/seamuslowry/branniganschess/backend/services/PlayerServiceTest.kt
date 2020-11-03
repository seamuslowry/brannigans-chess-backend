package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import javax.persistence.EntityNotFoundException

@ExtendWith(SpringExtension::class)
@WebMvcTest(PlayerService::class)
@AutoConfigureMockMvc(addFilters = false)
class PlayerServiceTest {
    @MockkBean
    lateinit var playerRepository: PlayerRepository
    @MockkBean
    lateinit var gameService: GameService

    @Autowired
    private lateinit var service: PlayerService

    @Test
    fun `gets an existing player`() {
        val authId = "old-auth-id"
        val providedPlayer = Player(authId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.of(providedPlayer)
        every { playerRepository.save(any<Player>()) } returns providedPlayer

        val player = service.authenticatePlayer(authId)
        verify(exactly = 1) { playerRepository.save(any<Player>()) }
        assertEquals(providedPlayer, player)
    }

    @Test
    fun `gets an new player`() {
        val authId = "new-auth-id"
        val providedPlayer = Player(authId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.empty()
        every { playerRepository.save(any<Player>()) } returns providedPlayer

        val player = service.authenticatePlayer(authId)
        verify(exactly = 1) { playerRepository.save(any<Player>()) }
        assertEquals(providedPlayer, player)
    }

    @Test
    fun `gets a players games`() {
        val authId = "games-id"
        val newPlayer = Player(authId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.of(newPlayer)
        every { gameService.findPlayerGames(any(), any(), any()) } returns listOf(game)

        val foundGames = service.getGames(newPlayer.authId, PieceColor.WHITE, true)

        verify(exactly = 1) { gameService.findPlayerGames(any(), any(), any()) }
        assertEquals(listOf(game), foundGames)
    }

    @Test
    fun `fails to get a non-existent player's games`() {
        val authId = "games-id"
        val newPlayer = Player(authId)
        every { playerRepository.findOne(any()) } returns Optional.empty()

        assertThrows<EntityNotFoundException> { service.getGames(newPlayer.authId, PieceColor.WHITE, true) }
    }

    @Test
    fun `joins a game`() {
        val authId = "games-id"
        val player = Player(authId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.of(player)
        every { gameService.addPlayer(any(), any(), any()) } returns game

        val newGame = service.joinGame(game.id, player.authId, null)

        verify(exactly = 1) { gameService.addPlayer(any(), any(), any()) }
        assertEquals(game, newGame)
    }

    @Test
    fun `leaves a game`() {
        val authId = "games-id"
        val player = Player(authId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.of(player)
        every { gameService.removePlayer(any(), any()) } returns game

        val newGame = service.leaveGame(game.id, player.authId)

        verify(exactly = 1) { gameService.removePlayer(any(), any()) }
        assertEquals(game, newGame)
    }
}
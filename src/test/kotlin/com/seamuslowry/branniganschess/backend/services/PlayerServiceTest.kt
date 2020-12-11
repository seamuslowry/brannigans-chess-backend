package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.PlayerStatInfo
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.test.context.support.WithMockUser
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
        verify(exactly = 0) { playerRepository.save(any<Player>()) }
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
    @WithMockUser(username = "new-name-id")
    fun `changes a player's name`() {
        val authId = "new-name-id"
        val newName = "newName"
        val preChange = Player(authId)
        val postChange = Player(authId, name=newName)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.of(preChange)
        every { playerRepository.save(any<Player>()) } returns postChange

        val player = service.changeName(authId, "new-name")
        verify(exactly = 1) { playerRepository.save(any<Player>()) }
        assertEquals(postChange, player)
    }

    @Test
    fun `gets a player's games`() {
        val authId = "games-id"
        val newPlayer = Player(authId)
        val game = Game()
        every { playerRepository.findById(any()) } returns Optional.of(newPlayer)
        every { gameService.findPlayerGames(any(), any(), any(), any()) } returns PageImpl(listOf(game))

        val foundGames = service.getGames(newPlayer.id, PieceColor.WHITE, pageable = Pageable.unpaged())

        verify(exactly = 1) { gameService.findPlayerGames(any(), any(), any(), any()) }
        assertEquals(listOf(game), foundGames.content)
    }

    @Test
    fun `fails to get a non-existent player's games`() {
        val authId = "games-id"
        val newPlayer = Player(authId)
        every { playerRepository.findById(any()) } returns Optional.empty()

        assertThrows<EntityNotFoundException> { service.getGames(newPlayer.id, PieceColor.WHITE, pageable = Pageable.unpaged()) }
    }

    @Test
    fun `counts a player's games`() {
        val count: Long = 5
        val authId = "count-games-id"
        val newPlayer = Player(authId)
        every { playerRepository.findById(any()) } returns Optional.of(newPlayer)
        every { gameService.countPlayerGames(any(), any(), any()) } returns count

        val stateInfo = service.getStats(newPlayer.id)

        verify(exactly = 6) { gameService.countPlayerGames(any(), any(), any()) }
        assertEquals(PlayerStatInfo(count,count,count,count,count,count), stateInfo)
    }

    @Test
    fun `joins a game`() {
        val authId = "games-id"
        val player = Player(authId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.of(player)
        every { gameService.addPlayer(any(), any(), any()) } returns game

        val newGame = service.joinGame(game.uuid, player.authId, null)

        verify(exactly = 1) { gameService.addPlayer(any(), any(), any()) }
        assertEquals(game, newGame)
    }

    @Test
    fun `non-existent player fails to join a game`() {
        val authId = "games-id"
        val player = Player(authId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.empty()
        every { gameService.addPlayer(any(), any(), any()) } returns game

        assertThrows<EntityNotFoundException> { service.joinGame(game.uuid, player.authId, null) }
    }

    @Test
    fun `leaves a game`() {
        val authId = "games-id"
        val player = Player(authId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.of(player)
        every { gameService.removePlayer(any(), any()) } returns game

        val newGame = service.leaveGame(game.uuid, player.authId)

        verify(exactly = 1) { gameService.removePlayer(any(), any()) }
        assertEquals(game, newGame)
    }
}
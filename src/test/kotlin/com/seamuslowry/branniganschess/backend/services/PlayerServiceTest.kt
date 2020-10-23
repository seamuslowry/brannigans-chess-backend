package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.Exception
import java.util.*


@ExtendWith(SpringExtension::class)
@WebMvcTest(PlayerService::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("unsecured")
class PlayerServiceTest {
    @MockkBean
    lateinit var playerRepository: PlayerRepository
    @MockkBean
    lateinit var gameService: GameService

    @Autowired
    private lateinit var service: PlayerService

    @Test
    fun `creates a player when it doesn't exist`() {
        val authId = "new-id"
        val newPlayer = Player(authId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returnsMany listOf(Optional.empty(), Optional.of(newPlayer))

        val savedPlayer = service.getOrCreate(authId)

        verify(exactly = 1) { playerRepository.save(any<Player>()) }
        assertEquals(newPlayer , savedPlayer)
    }

    @Test
    fun `does not create a player when it exists`() {
        val authId = "exists-id"
        val newPlayer = Player(authId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.of(newPlayer)

        val savedPlayer = service.getOrCreate(authId)

        verify(exactly = 0) { playerRepository.save(any<Player>()) }
        assertEquals(newPlayer , savedPlayer)
    }

    @Test
    fun `handles creation in a separate thread`() {
        val authId = "kinda-exists-id"
        val newPlayer = Player(authId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returnsMany listOf(Optional.empty(), Optional.of(newPlayer))
        every { playerRepository.save(any<Player>()) } throws Exception("message")

        val savedPlayer = service.getOrCreate(authId)

        verify(exactly = 1) { playerRepository.save(any<Player>()) }
        assertEquals(newPlayer , savedPlayer)
    }

    @Test
    fun `gets a players games`() {
        val authId = "games-id"
        val newPlayer = Player(authId)
        val game = Game()
        every { gameService.findPlayerGames(any(), any(), any()) } returns listOf(game)

        val foundGames = service.getGames(newPlayer, PieceColor.WHITE, true)

        verify(exactly = 1) { gameService.findPlayerGames(any(), any(), any()) }
        assertEquals(listOf(game), foundGames)
    }
}
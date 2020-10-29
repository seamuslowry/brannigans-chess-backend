package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.SignupException
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
    fun `already exists google signup`() {
        val googleId = "old-google-id"
        val newPlayer = Player(googleId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.of(newPlayer)

        assertThrows<SignupException> { service.googleSignup(googleId) }
        verify(exactly = 0) { playerRepository.save(any<Player>()) }
    }

    @Test
    fun `successful google signup`() {
        val googleId = "new-google-id"
        val newPlayer = Player(googleId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.empty()
        every { playerRepository.save(any<Player>()) } returns newPlayer

        val savedPlayer = service.googleSignup(googleId)
        verify(exactly = 1) { playerRepository.save(any<Player>()) }
        assertEquals(newPlayer, savedPlayer)
    }

    @Test
    fun `does not exist google login`() {
        val googleId = "new-google-id"
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.empty()

        assertThrows<SignupException> { service.googleLogin(googleId) }
    }

    @Test
    fun `successful google login`() {
        val googleId = "old-google-id"
        val oldPlayer = Player(googleId)
        every { playerRepository.findOne(any<Specification<Player>>()) } returns Optional.of(oldPlayer)

        val returnedPlayer = service.googleLogin(googleId)
        verify(exactly = 0) { playerRepository.save(any<Player>()) }
        assertEquals(oldPlayer, returnedPlayer)
    }

    @Test
    fun `gets a players games`() {
        val googleId = "games-id"
        val newPlayer = Player(googleId)
        val game = Game()
        every { playerRepository.findOne(any()) } returns Optional.of(newPlayer)
        every { gameService.findPlayerGames(any(), any(), any()) } returns listOf(game)

        val foundGames = service.getGames(newPlayer.googleId, PieceColor.WHITE, true)

        verify(exactly = 1) { gameService.findPlayerGames(any(), any(), any()) }
        assertEquals(listOf(game), foundGames)
    }
}
package com.seamuslowry.branniganschess.backend.controllers

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.Player
import com.seamuslowry.branniganschess.backend.services.PlayerService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ExtendWith(SpringExtension::class)
@WebMvcTest(PlayerController::class)
@AutoConfigureMockMvc
class PlayerControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var playerService: PlayerService

    @Test
    fun `Searches for games`() {
        val game = Game("Player Controller Test Game")
        every { playerService.getGames(any(), any(), any()) } returns listOf(game)

        mockMvc.get("/players/games?active=false&color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$") { isArray }
        }
    }

    @Test
    fun `gets a player`() {
        val authId = "test-player-get"
        val player = Player(authId)
        every { playerService.getPlayer(any()) } returns player

        mockMvc.get("/players/auth") {
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }
}
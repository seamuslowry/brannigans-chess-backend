package com.seamuslowry.branniganschess.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.AdditionalPlayerInfo
import com.seamuslowry.branniganschess.backend.dtos.ChangeNameDto
import com.seamuslowry.branniganschess.backend.dtos.PlayerStatInfo
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.Player
import com.seamuslowry.branniganschess.backend.services.PlayerService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ExtendWith(SpringExtension::class)
@WebMvcTest(PlayerController::class)
@AutoConfigureMockMvc
class PlayerControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var playerService: PlayerService

    @Test
    fun `Searches for games`() {
        val game = Game("Player Controller Test Game")
        every { playerService.getGames(any(), any(), any(), any()) } returns PageImpl(listOf(game))

        mockMvc.get("/players/games/1?active=false&color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.content") { isArray }
        }
    }

    @Test
    fun `Gets status information for games`() {
        every { playerService.getStats(any()) } returns PlayerStatInfo()

        mockMvc.get("/players/stats/1") {
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `gets a player`() {
        val authId = "test-player-get"
        val player = Player(authId)
        every { playerService.authenticatePlayer(any()) } returns player

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo())
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `updates a player name`() {
        val authId = "test-player-get"
        val player = Player(authId)
        every { playerService.changeName(any(), any()) } returns player

        mockMvc.post("/players/name") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(ChangeNameDto())
            accept = MediaType.APPLICATION_JSON
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }


    @Test
    fun `joins a game`() {
        val game = Game("Player Controller Join Game")

        every { playerService.joinGame(any(), any(), any()) } returns game

        mockMvc.post("/players/join/1?color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `leaves a game`() {
        val game = Game("Player Controller Leave Game")

        every { playerService.leaveGame(any(), any()) } returns game

        mockMvc.post("/players/leave/1") {
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }
}
package com.seamuslowry.branniganschess.backend.controllers

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.services.GameService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(GameController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("unsecured")
class GameControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var gameService: GameService

    @Test
    fun `Create a game`() {
        val newGame = Game("Controller Test Game")
        every { gameService.createGame() } returns newGame
        mockMvc.perform(post("/games/create").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$.uuid").value(newGame.uuid))
    }

    @Test
    fun `Searches for games`() {
        val game = Game("Game Controller Search Game")
        every { gameService.findAllBy(any<Boolean>(), any<Pageable>()) } returns PageImpl(listOf(game))
        mockMvc.perform(MockMvcRequestBuilders.get("/games?active=${game.winner != null}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$.content").isArray)
    }
}
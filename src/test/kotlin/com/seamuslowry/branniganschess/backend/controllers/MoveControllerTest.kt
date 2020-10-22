package com.seamuslowry.branniganschess.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.MoveService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MoveController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("unsecured")
class MoveControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var gameService: GameService

    @MockkBean
    private lateinit var moveService: MoveService

    @Test
    fun `Handles a move request`() {
        val piece = Pawn(PieceColor.BLACK, Game("move piece game"))
        val move =  Move(piece, 0,0,0,0)

        val moveRequest = ObjectMapper().writeValueAsString(MoveRequest(0,0,0,0))

        every { gameService.move(any(), any()) } returns move
        mockMvc.perform(post("/moves/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moveRequest)
        ).andExpect(status().isOk)
    }

    @Test
    fun `Searches for moves`() {
        val game = Game("Move Controller Test Game")
        game.id = 1
        val piece = Pawn(PieceColor.BLACK, game)
        val move = Move(piece, 0,0,0,0)
        every { moveService.findAllBy(game.id, piece.color) } returns listOf(move)
        mockMvc.perform(MockMvcRequestBuilders.get("/moves/${game.id}?color=${piece.color}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$").isArray)
    }
}
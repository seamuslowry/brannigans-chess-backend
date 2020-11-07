package com.seamuslowry.branniganschess.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.PieceIdentifierDto
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.models.pieces.Queen
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(PieceController::class)
@AutoConfigureMockMvc(addFilters = false)
class PieceControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var pieceService: PieceService

    @MockkBean
    private lateinit var gameService: GameService

    @Test
    fun `Searches for pieces without a color`() {
        val game = Game("Piece Controller Test Game")
        game.id = 1
        val piece = Pawn(PieceColor.BLACK, game)
        every { pieceService.findAllBy(game.id, emptyList(), piece.status) } returns emptyList()
        mockMvc.perform(get("/pieces/${game.id}?status=${piece.status}").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$").isArray)
            .andExpect(jsonPath("\$").isEmpty)
    }

    @Test
    fun `Searches for pieces of a single color`() {
        val game = Game("Piece Controller Test Game")
        game.id = 1
        val piece = Pawn(PieceColor.BLACK, game)
        every { pieceService.findAllBy(game.id, listOf(PieceColor.BLACK), piece.status) } returns listOf(piece)
        mockMvc.perform(get("/pieces/${game.id}?color=${piece.color}&status=${piece.status}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$").isArray)
                .andExpect(jsonPath("\$").isNotEmpty)
    }

    @Test
    fun `Searches for pieces of both colors`() {
        val game = Game("Piece Controller Test Game")
        game.id = 1
        val piece = Pawn(PieceColor.BLACK, game)
        every { pieceService.findAllBy(game.id, listOf(PieceColor.WHITE, PieceColor.BLACK), piece.status) } returns listOf(piece)
        mockMvc.perform(get("/pieces/${game.id}?color=WHITE&color=BLACK&status=${piece.status}").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$").isArray)
            .andExpect(jsonPath("\$").isNotEmpty)
    }

    @Test
    fun `Promotes a piece`() {
        val game = Game("Piece Controller Test Game")
        game.id = 1
        val piece = Queen(PieceColor.BLACK, game, 7, 0)
        val pieceIdRequest = ObjectMapper().writeValueAsString(PieceIdentifierDto(game.id, 7,0))

        every { pieceService.promote(any(), any()) } returns piece
        every { gameService.updateGameStatusForNextPlayer(any<Long>(), any()) } returns Game("Promote Game")
        mockMvc.perform(post("/pieces/promote/QUEEN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(pieceIdRequest))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$").isNotEmpty)
    }
}
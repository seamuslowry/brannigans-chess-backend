package com.seamuslowry.branniganschess.backend.controllers

import com.ninjasquad.springmockk.MockkBean
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
        val gameId = 1L
        val piece = Pawn(PieceColor.BLACK, gameId)
        every { pieceService.findAllBy(gameId, emptyList(), piece.status) } returns emptyList()
        mockMvc.perform(get("/pieces/${gameId}?status=${piece.status}").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$").isArray)
            .andExpect(jsonPath("\$").isEmpty)
    }

    @Test
    fun `Searches for pieces of a single color`() {
        val gameId = 1L
        val piece = Pawn(PieceColor.BLACK, gameId)
        every { pieceService.findAllBy(gameId, listOf(PieceColor.BLACK), piece.status) } returns listOf(piece)
        mockMvc.perform(get("/pieces/${gameId}?color=${piece.color}&status=${piece.status}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$").isArray)
                .andExpect(jsonPath("\$").isNotEmpty)
    }

    @Test
    fun `Searches for pieces of both colors`() {
        val gameId = 1L
        val piece = Pawn(PieceColor.BLACK, gameId)
        every { pieceService.findAllBy(gameId, listOf(PieceColor.WHITE, PieceColor.BLACK), piece.status) } returns listOf(piece)
        mockMvc.perform(get("/pieces/${gameId}?color=WHITE&color=BLACK&status=${piece.status}").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$").isArray)
            .andExpect(jsonPath("\$").isNotEmpty)
    }

    @Test
    fun `Promotes a piece`() {
        val gameId = 1L
        val piece = Queen(PieceColor.BLACK, gameId, 7, 0)

        every { pieceService.promote(any(), any()) } returns piece
        every { gameService.updateGameStatusForNextPlayer(any<Long>(), any()) } returns Game("Promote Game")
        mockMvc.perform(post("/pieces/promote/1/QUEEN"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$").isNotEmpty)
    }
}
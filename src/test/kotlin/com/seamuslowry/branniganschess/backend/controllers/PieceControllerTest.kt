package com.seamuslowry.branniganschess.backend.controllers

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(PieceController::class)
@AutoConfigureMockMvc(addFilters = false)
class PieceControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var pieceService: PieceService

    @Test
    fun `Searches for pieces`() {
        val game = Game("Piece Controller Test Game")
        game.id = 1
        val piece = Pawn(PieceColor.BLACK, game)
        every { pieceService.findAllBy(game.id, piece.color, piece.status) } returns listOf(piece)
        mockMvc.perform(get("/pieces/${game.id}?color=${piece.color}&status=${piece.status}").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("\$").isArray)
    }
}
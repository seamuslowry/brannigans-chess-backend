package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.branniganschess.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(MoveController::class)
@AutoConfigureMockMvc(addFilters = false)
class MoveControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var gameService: GameService

    @Test
    fun `Handles a move request`() {
        val piece = Piece(PieceType.PAWN, PieceColor.BLACK, Game("move piece game"))
        val move =  Move(piece, 0,0,0,0)

        val moveRequest = ObjectMapper().writeValueAsString(MoveRequest(0,0,0,0))

        every { gameService.move(any<Long>(), any<MoveRequest>()) } returns move
        mockMvc.perform(post("/moves/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moveRequest)
        ).andExpect(status().isOk)
    }
}
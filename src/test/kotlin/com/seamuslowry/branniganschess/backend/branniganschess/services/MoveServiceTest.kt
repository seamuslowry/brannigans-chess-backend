package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@WebMvcTest(MoveService::class)
@AutoConfigureMockMvc(addFilters = false)
class MoveServiceTest {
    @MockkBean
    lateinit var moveRepository: MoveRepository

    @Autowired
    private lateinit var service: MoveService

    @Test
    fun `creates a move`() {
        val game = Game("Piece Game")
        val piece = Piece(PieceType.PAWN, PieceColor.BLACK, game, 0, 0)
        val move = Move(
                piece,
                0,
                0,
                0,
                0
        )
        every { moveRepository.save(any<Move>()) } returns move

        val newMove = service.createMove(move)

        verify(exactly = 1) { moveRepository.save(any<Move>()) }
        assertEquals(move , newMove)
    }
}
package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
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
        val game = Game("Create Move Game")
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
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

    @Test
    fun `searches for moves`() {
        val game = Game("Search Move Game")
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
        val move = Move(piece, 0,0,0,0)
        every { moveRepository.findAll(any<Specification<Move>>()) } returns listOf(move)

        val foundPieces = service.findAllBy(1)

        verify(exactly = 1) { moveRepository.findAll(any<Specification<Move>>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `searches for a specific color of moves`() {
        val game = Game("Search Move Game")
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
        val move = Move(piece, 0,0,0,0)
        every { moveRepository.findAll(any<Specification<Move>>()) } returns listOf(move)

        val foundPieces = service.findAllBy(1, piece.color)

        verify(exactly = 1) { moveRepository.findAll(any<Specification<Move>>()) }
        assertEquals(1 , foundPieces.count())
    }
}
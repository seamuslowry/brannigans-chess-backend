package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.repos.MoveRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@WebMvcTest(MoveService::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("unsecured")
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

    @Test
    fun `searches for the last move`() {
        val game = Game("Search Last Move Game")
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
        val moveOne = Move(piece, 0,0,1,0)
        val moveTwo = Move(piece, 1,0,2,0)
        every { moveRepository.findAll(any<Specification<Move>>()) } returns listOf(moveOne, moveTwo)

        val foundMove = service.findLastMove(1)

        verify(exactly = 1) { moveRepository.findAll(any<Specification<Move>>()) }
        assertEquals(moveTwo , foundMove)
    }

    @Test
    fun `searches for the last move when there is none`() {
        every { moveRepository.findAll(any<Specification<Move>>()) } returns emptyList()

        val foundMove = service.findLastMove(1)

        verify(exactly = 1) { moveRepository.findAll(any<Specification<Move>>()) }
        assertNull(foundMove)
    }

    @Test
    fun `checks if a piece has moved`() {
        val game = Game("Has Moved Game")
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
        val move = Move(piece, 0,0,1,0)
        every { moveRepository.findAll(any<Specification<Move>>()) } returns listOf(move)

        val hasMoved = service.hasMoved(piece)

        verify(exactly = 1) { moveRepository.findAll(any<Specification<Move>>()) }
        assertTrue(hasMoved)
    }
}
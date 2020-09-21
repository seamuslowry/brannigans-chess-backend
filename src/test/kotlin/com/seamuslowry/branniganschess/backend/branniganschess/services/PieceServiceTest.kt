package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceType
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
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
@WebMvcTest(PieceService::class)
@AutoConfigureMockMvc(addFilters = false)
class PieceServiceTest {
    @MockkBean
    lateinit var pieceRepository: PieceRepository

    @Autowired
    private lateinit var service: PieceService

    @Test
    fun `creates a piece`() {
        val game = Game("Piece Game")
        val piece = Piece(PieceType.PAWN, PieceColor.BLACK, game, 0, 0)
        every { pieceRepository.save(any<Piece>()) } returns piece

        val newPiece = service.createPiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }
        assertEquals(piece , newPiece)
    }

    @Test
    fun `searches for a piece`() {
        val game = Game("Piece Game")
        val piece = Piece(PieceType.PAWN, PieceColor.BLACK, game, 0, 0)
        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns listOf(piece)

        val foundPieces = service.findAllBy(1, piece.color, piece.taken)

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }
        assertEquals(1 , foundPieces.count())
    }
}
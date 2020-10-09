package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.Piece
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.PieceStatus
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.repos.PieceRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
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
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
        every { pieceRepository.save(any<Piece>()) } returns piece

        val newPiece = service.createPiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }
        assertEquals(piece , newPiece)
        assertEquals(piece.game, game)
    }

    @Test
    fun `searches for a piece`() {
        val game = Game("Piece Game")
        val piece = Pawn( PieceColor.BLACK, game, 0, 0)
        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns listOf(piece)

        val foundPieces = service.findAllBy(1, piece.color, piece.status, piece.type)

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `returns active pieces as a 2D array`() {
        val game = Game("2D Piece Game")
        val pieces = listOf<Piece>(
                Pawn( PieceColor.BLACK, game, 7, 3),
                Pawn( PieceColor.BLACK, game, 0, 3),
                Pawn( PieceColor.BLACK, game, 1, 0),
                Pawn( PieceColor.BLACK, game, 4, 4)
        )

        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns pieces

        val board = service.getPiecesAsBoard(1)

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }

        for (piece in pieces) {
            assertNotNull(board[piece.positionRow!!][piece.positionCol!!])
        }
    }

    @Test
    fun `takes a piece`() {
        val game = Game("2D Piece Game")
        val piece = Pawn( PieceColor.BLACK, game, 4, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}

        val takenPiece = service.takePiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }

        assertEquals(PieceStatus.TAKEN, takenPiece.status)
        assertNull(takenPiece.positionCol)
        assertNull(takenPiece.positionRow)
    }

    @Test
    fun `removes a piece`() {
        val game = Game("2D Piece Game")
        val piece = Pawn( PieceColor.BLACK, game, 4, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}

        val takenPiece = service.removePiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }

        assertEquals(PieceStatus.REMOVED, takenPiece.status)
        assertNull(takenPiece.positionCol)
        assertNull(takenPiece.positionRow)
    }

    @Test
    fun `moves a piece`() {
        val game = Game("2D Piece Game")
        val piece = Pawn( PieceColor.BLACK, game, 4, 4)
        val newRow = 5
        val newCol = 6

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}

        val movedPiece = service.movePiece(piece, newRow, newCol)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }

        assertEquals(newCol, movedPiece.positionCol)
        assertEquals(newRow, movedPiece.positionRow)
    }

    @Test
    fun `finds a piece by location`() {
        val game = Game("Location Piece Game")
        val piece = Pawn( PieceColor.BLACK, game, 4, 4)

        every { pieceRepository.findAll( any<Specification<Piece>>()) } returns listOf(piece)

        val foundPiece = service.getPieceAt(game.id, 4, 4)

        assertEquals(piece, foundPiece)
    }
}
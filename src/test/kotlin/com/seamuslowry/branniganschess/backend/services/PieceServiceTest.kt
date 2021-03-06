package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.repos.PieceRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import javax.persistence.EntityNotFoundException
import kotlin.NoSuchElementException

@ExtendWith(SpringExtension::class)
@WebMvcTest(PieceService::class)
@AutoConfigureMockMvc(addFilters = false)
class PieceServiceTest {
    @MockkBean
    lateinit var pieceRepository: PieceRepository

    @Autowired
    private lateinit var service: PieceService

    private val blackPlayer = Player("blackPlayer")

    @Test
    fun `creates a piece`() {
        val gameId = 1L
        val piece = Pawn( PieceColor.BLACK, gameId, 0, 0)
        every { pieceRepository.save(any<Piece>()) } returns piece

        val newPiece = service.createPiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }
        assertEquals(piece , newPiece)
        assertEquals(piece.gameId, gameId)
    }

    @Test
    fun `gets a piece by id`() {
        val gameId = 1L
        val piece = Pawn( PieceColor.BLACK, gameId, 0, 0)
        every { pieceRepository.findById(any()) } returns Optional.of(piece)

        val foundPiece = service.getById(piece.id)

        assertEquals(piece , foundPiece)
    }

    @Test
    fun `throws when getting a piece that doesn't exist`() {
        val gameId = 1L
        val piece = Pawn( PieceColor.BLACK, gameId, 0, 0)
        every { pieceRepository.findById(any()) } returns Optional.empty()

        assertThrows<NoSuchElementException> { service.getById(piece.id) }
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `searches for a piece with authentication`() {
        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns emptyList()

        val foundPieces = service.findAllBy(Game("test", blackPlayer = blackPlayer), emptyList())

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }
        assertEquals(0 , foundPieces.count())
    }

    @Test
    fun `searches for a piece without colors`() {
        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns emptyList()

        val foundPieces = service.findAllBy(1, emptyList())

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }
        assertEquals(0 , foundPieces.count())
    }

    @Test
    fun `searches for a piece by one color`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 0, 0)
        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns listOf(piece)

        val foundPieces = service.findAllBy(1, piece.color, piece.status, piece.type)

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `searches for a piece by multiple colors`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 0, 0)
        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns listOf(piece)

        val foundPieces = service.findAllBy(1, listOf(PieceColor.BLACK, PieceColor.WHITE), piece.status, piece.type)

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `returns active pieces as a 2D array`() {
        val gameId = 1L
        val pieces = listOf<Piece>(
                Pawn( PieceColor.BLACK, gameId, 7, 3),
                Pawn( PieceColor.BLACK, gameId, 0, 3),
                Pawn( PieceColor.BLACK, gameId, 1, 0),
                Pawn( PieceColor.BLACK, gameId, 4, 4)
        )

        every { pieceRepository.findAll(any<Specification<Piece>>()) } returns pieces

        val board = service.getPiecesAsBoard(1)

        verify(exactly = 1) { pieceRepository.findAll(any<Specification<Piece>>()) }

        for (piece in pieces) {
            assertNotNull(board[piece.positionRow][piece.positionCol])
        }
    }

    @Test
    fun `takes a piece`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 4, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}

        val takenPiece = service.takePiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }

        assertEquals(PieceStatus.TAKEN, takenPiece.status)
    }

    @Test
    fun `removes a piece`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 4, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}

        val takenPiece = service.removePiece(piece)

        verify(exactly = 1) { pieceRepository.save(any<Piece>()) }

        assertEquals(PieceStatus.REMOVED, takenPiece.status)
    }

    @Test
    fun `moves a piece`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 4, 4)
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
        val gameId = 1L
        val piece = Pawn( PieceColor.BLACK, gameId, 4, 4)

        every { pieceRepository.findAll( any<Specification<Piece>>()) } returns listOf(piece)

        val foundPiece = service.getPieceAt(gameId, 4, 4)

        assertEquals(piece, foundPiece)
    }

    @Test
    fun `throw when cannot find a piece by location`() {
        val gameId = 1L

        every { pieceRepository.findAll( any<Specification<Piece>>()) } returns emptyList()

        assertThrows<EntityNotFoundException> { service.getPieceAt(gameId, 4, 4) }
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `promotes a piece to queen`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        val promotedPiece = service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.QUEEN)

        verify(exactly = 2) { pieceRepository.save(any<Piece>()) }
        assertTrue(promotedPiece is Queen)
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `promotes a piece to knight`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        val promotedPiece = service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.KNIGHT)

        verify(exactly = 2) { pieceRepository.save(any<Piece>()) }
        assertTrue(promotedPiece is Knight)
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `promotes a piece to bishop`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        val promotedPiece = service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.BISHOP)

        verify(exactly = 2) { pieceRepository.save(any<Piece>()) }
        assertTrue(promotedPiece is Bishop)
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `promotes a piece to rook`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        val promotedPiece = service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.ROOK)

        verify(exactly = 2) { pieceRepository.save(any<Piece>()) }
        assertTrue(promotedPiece is Rook)
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `will not promote a piece to king`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        assertThrows<ChessRuleException> { service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.KING) }
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `will not promote a piece to pawn`() {
        val piece = Pawn( PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        assertThrows<ChessRuleException> { service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.KING) }
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `will not promote a non-pawn`() {
        val piece = Knight(PieceColor.BLACK, 1L, 7, 4)

        every { pieceRepository.save(any<Piece>()) } answers {firstArg()}
        every { pieceRepository.findByIdOrNull(any()) } returns piece

        assertThrows<ChessRuleException> { service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.QUEEN) }
    }

    @Test
    @WithMockUser(username = "blackPlayer")
    fun `will not promote a pawn in an early row`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 6, 4)

        assertThrows<ChessRuleException> { service.promote(piece, Game("test", blackPlayer = blackPlayer), PieceType.QUEEN) }
    }
}
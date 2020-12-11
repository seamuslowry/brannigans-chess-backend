package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class KingTest {
    @Test
    fun `can plausibly move to eight locations when unimpeded by the edges`() {
        val piece = King(PieceColor.BLACK, Game(""), 3, 3)

        assertTrue(piece.canMove(Position(3,2)))
        assertTrue(piece.canMove(Position(3,4)))
        assertTrue(piece.canMove(Position(2,2)))
        assertTrue(piece.canMove(Position(2,3)))
        assertTrue(piece.canMove(Position(2,4)))
        assertTrue(piece.canMove(Position(4,2)))
        assertTrue(piece.canMove(Position(4,3)))
        assertTrue(piece.canMove(Position(4,4)))
    }

    @Test
    fun `cannot move off the board`() {
        val piece = King(PieceColor.BLACK, Game(""), 0, 0)

        assertFalse(piece.canMove(Position(-1,-1)))
    }

    @Test
    fun `captures where it can move`() {
        val piece = King(PieceColor.BLACK, Game(""), 3, 3)

        assertTrue(piece.canCapture(Position(3,2)))
        assertTrue(piece.canCapture(Position(3,4)))
        assertTrue(piece.canCapture(Position(2,2)))
        assertTrue(piece.canCapture(Position(2,3)))
        assertTrue(piece.canCapture(Position(2,4)))
        assertTrue(piece.canCapture(Position(4,2)))
        assertTrue(piece.canCapture(Position(4,3)))
        assertTrue(piece.canCapture(Position(4,4)))
    }

    @Test
    fun `requires no fields be empty`() {
        val piece = King(PieceColor.BLACK, Game(""), 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(1,1))
        assertEquals(setOf<Position>(), requiresEmpty)
    }

    @Test
    fun `provides the tile that would request a king side castle - BLACK`() {
        val piece = King(PieceColor.BLACK, Game(""))

        assertEquals(Position(0,6), piece.kingSideCastleRequest())
    }

    @Test
    fun `provides the tile that would request a king side castle - WHITE`() {
        val piece = King(PieceColor.WHITE, Game(""))

        assertEquals(Position(7,6), piece.kingSideCastleRequest())
    }

    @Test
    fun `provides the tile of the rook for a king side castle - BLACK`() {
        val piece = King(PieceColor.BLACK, Game(""))

        assertEquals(Position(0,7), piece.kingSideCastleRook())
    }

    @Test
    fun `provides the tile of the rook a king side castle - WHITE`() {
        val piece = King(PieceColor.WHITE, Game(""))

        assertEquals(Position(7,7), piece.kingSideCastleRook())
    }

    @Test
    fun `provides the tile that would request a queen side castle - BLACK`() {
        val piece = King(PieceColor.BLACK, Game(""))

        assertEquals(Position(0,2), piece.queenSideCastleRequest())
    }

    @Test
    fun `provides the tile that would request a queen side castle - WHITE`() {
        val piece = King(PieceColor.WHITE, Game(""))

        assertEquals(Position(7,2), piece.queenSideCastleRequest())
    }

    @Test
    fun `provides the tile of the rook for a queen side castle - BLACK`() {
        val piece = King(PieceColor.BLACK, Game(""))

        assertEquals(Position(0,0), piece.queenSideCastleRook())
    }

    @Test
    fun `provides the tile of the rook a queen side castle - WHITE`() {
        val piece = King(PieceColor.WHITE, Game(""))

        assertEquals(Position(7,0), piece.queenSideCastleRook())
    }

    @Test
    fun `has a max of 8 plausible moves`() {
        val piece = King(PieceColor.BLACK, Game(""), 3, 3)

        assertEquals(setOf(
                Position(3,4),
                Position(3,2),
                Position(2,4),
                Position(2,2),
                Position(4,4),
                Position(4,2),
                Position(2,3),
                Position(4,3)
        ), piece.plausibleMoves())
    }

    @Test
    fun `has a min of 3 plausible moves`() {
        val piece = King(PieceColor.BLACK, Game(""), 0, 0)

        assertEquals(setOf(
                Position(0,1),
                Position(1,1),
                Position(1,0)
        ), piece.plausibleMoves())
    }
}
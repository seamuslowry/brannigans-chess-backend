package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PawnTest {
    @Test
    fun `BLACK - cannot move more than two spaces from the start`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 1, 0)

        assertFalse(piece.canMove(Position(4,0)))
    }

    @Test
    fun `BLACK - can move two spaces from the start`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 1, 0)

        assertTrue(piece.canMove(Position(3,0)))
    }

    @Test
    fun `BLACK - can move one space from the start`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 1, 0)

        assertTrue(piece.canMove(Position(2,0)))
    }

    @Test
    fun `BLACK - requires the intervening tile to be empty when moving two spaces`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(3,0))
        assertEquals(setOf(Position(2,0)), requiresEmpty)
    }

    @Test
    fun `BLACK - can usually move one space`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 4, 0)

        assertTrue(piece.canMove(Position(5,0)))
    }

    @Test
    fun `BLACK - cannot move backwards`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 4, 0)

        assertFalse(piece.canMove(Position(3,0)))
    }

    @Test
    fun `BLACK - cannot move at the end of the board`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 7, 0)

        assertFalse(piece.canMove(Position(8,0)))
    }

    @Test
    fun `BLACK - captures on diagonals`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 2, 2)

        assertTrue(piece.canCapture(Position(3,3)))
        assertTrue(piece.canCapture(Position(3,1)))
    }

    @Test
    fun `BLACK - cannot move on diagonals`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 2, 2)

        assertFalse(piece.canMove(Position(3,3)))
        assertFalse(piece.canMove(Position(3,1)))
    }

    @Test
    fun `WHITE - cannot move more than two spaces from the start`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 0)

        assertFalse(piece.canMove(Position(3,0)))
    }

    @Test
    fun `WHITE - can move two spaces from the start`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 0)

        assertTrue(piece.canMove(Position(4,0)))
    }

    @Test
    fun `WHITE - can move one spaces from the start`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 0)

        assertTrue(piece.canMove(Position(5,0)))
    }

    @Test
    fun `WHITE - requires the intervening tile to be empty when moving two spaces`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 0)

        val requiresEmpty = piece.requiresEmpty(Position(4,0))
        assertEquals(setOf(Position(5,0)), requiresEmpty)
    }

    @Test
    fun `WHITE - can usually move one space`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 4, 0)

        assertTrue(piece.canMove(Position(3,0)))
    }

    @Test
    fun `WHITE - cannot move backwards`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 4, 0)

        assertFalse(piece.canMove(Position(5,0)))
    }

    @Test
    fun `WHITE - cannot move at the end of the board`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 0, 0)

        assertFalse(piece.canMove(Position(-1,0)))
    }

    @Test
    fun `WHITE - captures on diagonals`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 2)

        assertTrue(piece.canCapture(Position(5,3)))
        assertTrue(piece.canCapture(Position(5,1)))
    }

    @Test
    fun `WHITE - cannot move on diagonals`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 2)

        assertFalse(piece.canMove(Position(5,3)))
        assertFalse(piece.canMove(Position(5,1)))
    }

    @Test
    fun `WHITE - has a max of 4 plausible moves`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 6, 2)

        assertEquals(setOf(
                Position(5, 3),
                Position(5,1),
                Position(5, 2),
                Position(4,2)
        ), piece.plausibleMoves())
    }

    @Test
    fun `WHITE - has no moves at the end of the board`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 0, 2)

        assertEquals(emptySet<Position>(), piece.plausibleMoves())
    }

    @Test
    fun `WHITE - 3 moves after leaving the starting position`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 5, 2)

        assertEquals(setOf(
                Position(4, 3),
                Position(4,1),
                Position(4, 2)
        ), piece.plausibleMoves())
    }

    @Test
    fun `WHITE - no plausible moves off the side of the board`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 5, 0)

        assertEquals(setOf(
                Position(4,1),
                Position(4, 0)
        ), piece.plausibleMoves())
    }

    @Test
    fun `BLACK - has a max of 4 plausible moves`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 1, 2)

        assertEquals(setOf(
                Position(2, 3),
                Position(2,1),
                Position(2, 2),
                Position(3,2)
        ), piece.plausibleMoves())
    }

    @Test
    fun `BLACK - has no moves at the end of the board`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 7, 2)

        assertEquals(emptySet<Position>(), piece.plausibleMoves())
    }

    @Test
    fun `BLACK - 3 moves after leaving the starting position`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 2, 2)

        assertEquals(setOf(
                Position(3, 3),
                Position(3,1),
                Position(3, 2)
        ), piece.plausibleMoves())
    }

    @Test
    fun `BLACK - no plausible moves off the side of the board`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 2, 0)

        assertEquals(setOf(
                Position(3,1),
                Position(3, 0)
        ), piece.plausibleMoves())
    }

    @Test
    fun `BLACK - is promotable at the final row`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 7, 0)

        assertTrue(piece.promotable())
    }

    @Test
    fun `WHITE - is promotable at the final row`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 0, 0)

        assertTrue(piece.promotable())
    }

    @Test
    fun `BLACK - is not promotable off the final row`() {
        val piece = Pawn(PieceColor.BLACK, 1L, 6, 0)

        assertFalse(piece.promotable())
    }

    @Test
    fun `WHITE - is not promotable off the final row`() {
        val piece = Pawn(PieceColor.WHITE, 1L, 1, 0)

        assertFalse(piece.promotable())
    }
}
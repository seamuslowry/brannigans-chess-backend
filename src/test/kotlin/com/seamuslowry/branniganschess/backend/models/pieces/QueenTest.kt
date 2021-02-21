package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class QueenTest {
    @Test
    fun `can move in any direction`() {
        val piece = Queen(PieceColor.BLACK, 1L, 3, 3)

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
        val piece = Queen(PieceColor.BLACK, 1L, 0, 0)

        assertFalse(piece.canMove(Position(-1,-1)))
    }

    @Test
    fun `captures where it can move`() {
        val piece = Queen(PieceColor.BLACK, 1L, 3, 3)

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
    fun `requires intervening fields be empty - 0 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 0, 0)

        val requiresEmpty = piece.requiresEmpty(Position(0,7))
        assertEquals(setOf(
                Position(0,1),
                Position(0,2),
                Position(0,3),
                Position(0,4),
                Position(0,5),
                Position(0,6)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - negative 45 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 0, 0)

        val requiresEmpty = piece.requiresEmpty(Position(7,7))
        assertEquals(setOf(
                Position(1,1),
                Position(2,2),
                Position(3,3),
                Position(4,4),
                Position(5,5),
                Position(6,6)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - negative 90 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 0, 0)

        val requiresEmpty = piece.requiresEmpty(Position(7,0))
        assertEquals(setOf(
                Position(1,0),
                Position(2,0),
                Position(3,0),
                Position(4,0),
                Position(5,0),
                Position(6,0)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - negative 135 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 0, 7)

        val requiresEmpty = piece.requiresEmpty(Position(7,0))
        assertEquals(setOf(
                Position(1,6),
                Position(2,5),
                Position(3,4),
                Position(4,3),
                Position(5,2),
                Position(6,1)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - 180 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 0, 7)

        val requiresEmpty = piece.requiresEmpty(Position(0,0))
        assertEquals(setOf(
                Position(0,6),
                Position(0,5),
                Position(0,4),
                Position(0,3),
                Position(0,2),
                Position(0,1)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - 135 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 7, 7)

        val requiresEmpty = piece.requiresEmpty(Position(0,0))
        assertEquals(setOf(
                Position(6,6),
                Position(5,5),
                Position(4,4),
                Position(3,3),
                Position(2,2),
                Position(1,1)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - 90 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 7, 0)

        val requiresEmpty = piece.requiresEmpty(Position(0,0))
        assertEquals(setOf(
                Position(6,0),
                Position(5,0),
                Position(4,0),
                Position(3,0),
                Position(2,0),
                Position(1,0)
        ), requiresEmpty)
    }

    @Test
    fun `requires intervening fields be empty - 45 degrees`() {
        val piece = Queen(PieceColor.BLACK, 1L, 7, 0)

        val requiresEmpty = piece.requiresEmpty(Position(0,7))
        assertEquals(setOf(
                Position(6,1),
                Position(5,2),
                Position(4,3),
                Position(3,4),
                Position(2,5),
                Position(1,6)
        ), requiresEmpty)
    }

    @Test
    fun `can plausibly move along any straight line or diagonal`() {
        val piece = Queen(PieceColor.BLACK, 1L, 3, 3)

        assertEquals(setOf(
                Position(3,0),
                Position(3,1),
                Position(3,2),
                Position(3,4),
                Position(3,5),
                Position(3,6),
                Position(3,7),
                Position(7,3),
                Position(6,3),
                Position(5,3),
                Position(4,3),
                Position(2,3),
                Position(1,3),
                Position(0,3),
                Position(0,0),
                Position(1,1),
                Position(2,2),
                Position(4,4),
                Position(5,5),
                Position(6,6),
                Position(7,7),
                Position(6,0),
                Position(5,1),
                Position(4,2),
                Position(2,4),
                Position(1,5),
                Position(0,6)
        ), piece.plausibleMoves())
    }
}
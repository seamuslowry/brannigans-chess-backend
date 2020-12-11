package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class RookTest {
    @Test
    fun `can move in straight lines`() {
        val piece = Rook(PieceColor.BLACK, Game(""), 3, 3)

        for (v in 0..7) {
            assertTrue(piece.canMove(Position(3,v)))
            assertTrue(piece.canMove(Position(v,3)))
        }
    }

    @Test
    fun `cannot move off the board`() {
        val piece = Rook(PieceColor.BLACK, Game(""), 0, 0)

        assertFalse(piece.canMove(Position(-1,0)))
    }

    @Test
    fun `captures in straight lines`() {
        val piece = Rook(PieceColor.BLACK, Game(""), 3, 3)

        for (v in 0..7) {
            assertTrue(piece.canCapture(Position(3,v)))
            assertTrue(piece.canCapture(Position(v,3)))
        }
    }

    @Test
    fun `requires tiles in the lines it would move to be empty - horizontal`() {
        val piece = Rook(PieceColor.BLACK, Game(""), 0, 0)

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
    fun `requires tiles in the lines it would move to be empty - vertical`() {
        val piece = Rook(PieceColor.BLACK, Game(""), 0, 0)

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
    fun `can plausibly move along any straight line`() {
        val piece = Rook(PieceColor.BLACK, Game(""), 3, 3)

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
                Position(0,3)
        ), piece.plausibleMoves())
    }
}
package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 3, 3)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 0, 0)

        assertFalse(piece.canMove(Position(-1,-1)))
    }

    @Test
    fun `captures where it can move`() {
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 3, 3)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 0, 0)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 0, 0)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 0, 0)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 0, 7)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 0, 7)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 7, 7)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 7, 0)

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
        val game = Game("New Game")
        val piece = Queen(PieceColor.BLACK, game, 7, 0)

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
}
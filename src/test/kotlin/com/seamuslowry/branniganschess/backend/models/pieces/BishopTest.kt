package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class BishopTest {
    @Test
    fun `can move along diagonals`() {
        val game = Game("New Game")
        val piece = Bishop(PieceColor.BLACK, game, 3, 3)

        assertTrue(piece.canMove(Position(2,2)))
        assertTrue(piece.canMove(Position(2,4)))
        assertTrue(piece.canMove(Position(4,2)))
        assertTrue(piece.canMove(Position(4,4)))
    }

    @Test
    fun `cannot move off the board`() {
        val game = Game("New Game")
        val piece = Bishop(PieceColor.BLACK, game, 0, 0)

        assertFalse(piece.canMove(Position(-1,-1)))
    }

    @Test
    fun `captures where it can move`() {
        val game = Game("New Game")
        val piece = Bishop(PieceColor.BLACK, game, 3, 3)

        assertTrue(piece.canCapture(Position(2,2)))
        assertTrue(piece.canCapture(Position(2,4)))
        assertTrue(piece.canCapture(Position(4,2)))
        assertTrue(piece.canCapture(Position(4,4)))
    }

    @Test
    fun `requires fields along the diagonal it would travel be empty`() {
        val game = Game("New Game")
        val piece = Bishop(PieceColor.BLACK, game, 0, 0)

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
    fun `can plausibly move along any diagonal`() {
        val game = Game("New Game")
        val piece = Bishop(PieceColor.BLACK, game, 3, 3)

        assertEquals(setOf(
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
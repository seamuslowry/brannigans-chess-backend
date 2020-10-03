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
        val game = Game("New Game")
        val piece = King(PieceColor.BLACK, game, 3, 3)

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
        val piece = King(PieceColor.BLACK, game, 0, 0)

        assertFalse(piece.canMove(Position(-1,-1)))
    }

    @Test
    fun `captures where it can move`() {
        val game = Game("New Game")
        val piece = King(PieceColor.BLACK, game, 3, 3)

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
        val game = Game("New Game")
        val piece = King(PieceColor.BLACK, game, 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(1,1))
        assertEquals(setOf<Position>(), requiresEmpty)
    }
}
package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class KnightTest {
    @Test
    fun `can move to eight locations when unimpeded by edges`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 3, 3)

        assertTrue(piece.canMove(Position(1,2)))
        assertTrue(piece.canMove(Position(1,4)))
        assertTrue(piece.canMove(Position(2,1)))
        assertTrue(piece.canMove(Position(2,5)))
        assertTrue(piece.canMove(Position(4,5)))
        assertTrue(piece.canMove(Position(4,1)))
        assertTrue(piece.canMove(Position(5,2)))
        assertTrue(piece.canMove(Position(5,4)))
    }

    @Test
    fun `cannot move off the board`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 0, 0)

        assertFalse(piece.canMove(Position(-1,-2)))
    }

    @Test
    fun `can capture all locations it can move`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 3, 3)

        assertTrue(piece.canCapture(Position(1,2)))
        assertTrue(piece.canCapture(Position(1,4)))
        assertTrue(piece.canCapture(Position(2,1)))
        assertTrue(piece.canCapture(Position(2,5)))
        assertTrue(piece.canCapture(Position(4,5)))
        assertTrue(piece.canCapture(Position(4,1)))
        assertTrue(piece.canCapture(Position(5,2)))
        assertTrue(piece.canCapture(Position(5,4)))
    }

    @Test
    fun `requires no tiles to be empty`() {
        val game = Game("Knight Test game")
        val piece = Knight(PieceColor.BLACK, game, 1,0)

        assertEquals(emptySet<Position>(), piece.requiresEmpty(Position(3,1)))
    }
}
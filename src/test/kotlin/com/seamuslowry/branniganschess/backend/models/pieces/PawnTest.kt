package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PawnTest {
    @Test
    fun `BLACK - can move two spaces from the start`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 1, 0)

        assertTrue(piece.canMove(Position(3,0)))
    }

    @Test
    fun `BLACK - can move one space from the start`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 1, 0)

        assertTrue(piece.canMove(Position(2,0)))
    }

    @Test
    fun `BLACK - requires the intervening tile to be empty when moving two spaces`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(3,0))
        assertEquals(setOf(Position(2,0)), requiresEmpty)
    }

    @Test
    fun `BLACK - can usually move one space`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 4, 0)

        assertTrue(piece.canMove(Position(5,0)))
    }

    @Test
    fun `BLACK - cannot move at the end of the board`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 7, 0)

        assertFalse(piece.canMove(Position(8,0)))
    }

    @Test
    fun `BLACK - captures on diagonals`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 2, 2)

        assertTrue(piece.canCapture(Position(3,3)))
        assertTrue(piece.canCapture(Position(3,1)))
    }

    @Test
    fun `WHITE - can move two spaces from the start`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 0)

        assertTrue(piece.canMove(Position(4,0)))
    }

    @Test
    fun `WHITE - can move one spaces from the start`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 0)

        assertTrue(piece.canMove(Position(5,0)))
    }

    @Test
    fun `WHITE - requires the intervening tile to be empty when moving two spaces`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 0)

        val requiresEmpty = piece.requiresEmpty(Position(4,0));
        assertEquals(setOf(Position(5,0)), requiresEmpty)
    }

    @Test
    fun `WHITE - can usually move one space`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 4, 0)

        assertTrue(piece.canMove(Position(3,0)))
    }

    @Test
    fun `WHITE - cannot move at the end of the board`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 0, 0)

        assertFalse(piece.canMove(Position(-1,0)))
    }

    @Test
    fun `WHITE - captures on diagonals`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 2)

        assertTrue(piece.canCapture(Position(5,3)))
        assertTrue(piece.canCapture(Position(5,1)))
    }
}
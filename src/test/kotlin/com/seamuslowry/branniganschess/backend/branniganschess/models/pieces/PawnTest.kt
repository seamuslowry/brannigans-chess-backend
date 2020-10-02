package com.seamuslowry.branniganschess.backend.branniganschess.models.pieces

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PawnTest {
    @Test
    fun `BLACK - can plausibly move two spaces from the start`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 1, 0)

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf(Position(2,0), Position(3,0)), plausibleMoves)
    }

    @Test
    fun `BLACK - requires the intervening tile to be empty when moving two spaces`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(3,0));
        assertEquals(setOf(Position(2,0)), requiresEmpty)
    }

    @Test
    fun `BLACK - can usually move one space`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 2, 0)

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf(Position(3,0)), plausibleMoves)
    }

    @Test
    fun `BLACK - cannot move at the end of the board`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 7, 0)

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf<Position>(), plausibleMoves)
    }

    @Test
    fun `BLACK - captures on diagonals`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 2, 2)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf(Position(3, 1), Position(3, 3)), plausibleCaptures)
    }

    @Test
    fun `BLACK - captures on diagonals - queen edge`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 2, 0)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf(Position(3, 1)), plausibleCaptures)
    }

    @Test
    fun `BLACK - captures on diagonals - king edge`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 2, 7)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf(Position(3, 6)), plausibleCaptures)
    }

    @Test
    fun `WHITE - can plausibly move two spaces from the start`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 0)

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf(Position(5,0), Position(4,0)), plausibleMoves)
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

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf(Position(3,0)), plausibleMoves)
    }

    @Test
    fun `WHITE - cannot move at the end of the board`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 0, 0)

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf<Position>(), plausibleMoves)
    }

    @Test
    fun `WHITE - captures on diagonals`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 2)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf(Position(5, 1), Position(5, 3)), plausibleCaptures)
    }

    @Test
    fun `WHITE - captures on diagonals - queen edge`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 0)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf(Position(5, 1)), plausibleCaptures)
    }

    @Test
    fun `WHITE - captures on diagonals - king edge`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.WHITE, game, 6, 7)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf(Position(5, 6)), plausibleCaptures)
    }
}
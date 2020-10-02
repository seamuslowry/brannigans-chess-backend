package com.seamuslowry.branniganschess.backend.branniganschess.models.pieces

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class RookTest {
    @Test
    fun `NOT IMPLEMENTED - has no plausible moves`() {
        val game = Game("New Game")
        val piece = Rook(PieceColor.BLACK, game, 1, 0)

        val plausibleMoves = piece.plausibleMoves();
        assertEquals(setOf<Position>(), plausibleMoves)
    }

    @Test
    fun `NOT IMPLEMENTED - has no plausible captures`() {
        val game = Game("New Game")
        val piece = Rook(PieceColor.BLACK, game, 1, 0)

        val plausibleCaptures = piece.plausibleCaptures();
        assertEquals(setOf<Position>(), plausibleCaptures)
    }

    @Test
    fun `NOT IMPLEMENTED - requires no fields be empty`() {
        val game = Game("New Game")
        val piece = Rook(PieceColor.BLACK, game, 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(0,0));
        assertEquals(setOf<Position>(), requiresEmpty)
    }

    @Test
    fun `NOT IMPLEMENTED - is always allowed to move`() {
        val game = Game("New Game")
        val piece = Rook(PieceColor.BLACK, game, 1, 0)

        val canMove = piece.canMove(Position(0,0));
        assertTrue(canMove)
    }
}
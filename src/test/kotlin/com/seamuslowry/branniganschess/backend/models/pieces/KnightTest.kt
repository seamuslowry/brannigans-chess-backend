package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class KnightTest {
    @Test
    fun `can plausibly move to eight locations when unimpeded by the edges`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 3, 3)

        val plausibleMoves = piece.plausibleMoves()
        assertEquals(8, plausibleMoves.count())
        assertEquals(setOf(
                Position(1,2),
                Position(1,4),
                Position(2,1),
                Position(2,5),
                Position(4,5),
                Position(4,1),
                Position(5,2),
                Position(5,4)
        ), plausibleMoves)
    }

    @Test
    fun `cannot plausibly move off the edge from top-left`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 0, 0)

        val plausibleMoves = piece.plausibleMoves()
        assertEquals(2, plausibleMoves.count())
        assertEquals(setOf(
                Position(2,1),
                Position(1,2)
        ), plausibleMoves)
    }

    @Test
    fun `cannot plausibly move off the edge from top-right`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 0, 7)

        val plausibleMoves = piece.plausibleMoves()
        assertEquals(2, plausibleMoves.count())
        assertEquals(setOf(
                Position(2,6),
                Position(1,5)
        ), plausibleMoves)
    }

    @Test
    fun `cannot plausibly move off the edge from bottom-right`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 7, 7)

        val plausibleMoves = piece.plausibleMoves()
        assertEquals(2, plausibleMoves.count())
        assertEquals(setOf(
                Position(5,6),
                Position(6,5)
        ), plausibleMoves)
    }

    @Test
    fun `cannot plausibly move off the edge from bottom-left`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 7, 0)

        val plausibleMoves = piece.plausibleMoves()
        assertEquals(2, plausibleMoves.count())
        assertEquals(setOf(
                Position(5,1),
                Position(6,2)
        ), plausibleMoves)
    }

    @Test
    fun `captures where it can move`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 3, 3)

        val plausibleCaptures = piece.plausibleCaptures()
        val plausibleMoves = piece.plausibleMoves()
        assertEquals(plausibleMoves, plausibleCaptures)
    }

    @Test
    fun `requires no fields be empty`() {
        val game = Game("New Game")
        val piece = Knight(PieceColor.BLACK, game, 1, 0)

        val requiresEmpty = piece.requiresEmpty(Position(3,1))
        assertEquals(setOf<Position>(), requiresEmpty)
    }
}
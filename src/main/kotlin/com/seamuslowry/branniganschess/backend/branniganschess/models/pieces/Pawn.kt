package com.seamuslowry.branniganschess.backend.branniganschess.models.pieces

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import org.hibernate.event.spi.PostInsertEvent
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import kotlin.math.abs

@Entity
@DiscriminatorValue("PAWN")
class Pawn(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.PAWN, color, game, positionRow, positionCol, taken, id) {
    override fun plausibleCaptures(): Set<Position> {
        if (positionRow == 0 || positionRow == 7) return HashSet()
        if (positionRow == null || positionCol == null) return HashSet()
        if (taken) return HashSet();
        val row = positionRow ?: 0
        val col = positionCol ?: 0

        val set = HashSet<Position>()
        val direction = direction()
        if (col != 0) set.add(Position(row + 1 * direction, col - 1))
        if (col != 7) set.add(Position(row + 1 * direction, col + 1))

        return set
    }

    override fun plausibleMoves(): Set<Position> {
        if (positionRow == 0 || positionRow == 7) return HashSet()
        if (positionRow == null || positionCol == null) return HashSet()
        if (taken) return HashSet();
        val row = positionRow ?: 0
        val col = positionCol ?: 0

        val set = HashSet<Position>()
        val direction = direction()
        set.add(Position(row + 1 * direction, col))

        if (row == startingRow()) set.add(Position(row + 2 * direction, col))

        return set;
    }

    override fun requiresEmpty(dst: Position): Set<Position> {
        val direction = direction()
        val row = positionRow ?: 0
        val col = positionCol ?: 0
        if (abs(row - dst.row) == 2) return setOf(Position(row + 1 * direction, col))
        return HashSet()
    }

    private fun startingRow() = if (PieceColor.BLACK == color) 1 else 6

    private fun direction() = if (PieceColor.BLACK == color) 1 else -1
}
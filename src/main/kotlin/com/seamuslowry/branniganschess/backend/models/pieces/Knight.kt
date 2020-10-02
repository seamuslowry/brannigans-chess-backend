package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("KNIGHT")
class Knight(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.KNIGHT, color, game, positionRow, positionCol, taken, id) {
    override fun plausibleCaptures(): Set<Position> = plausibleMoves()

    override fun plausibleMoves(): Set<Position> {
        if (isImmovable()) return emptySet()
        val (row, col) = position() ?: return emptySet()

        val set = HashSet<Position>()
        set.add(Position(row - 2, col + 1))
        set.add(Position(row - 2, col - 1))
        set.add(Position(row + 2, col + 1))
        set.add(Position(row + 2, col - 1))
        set.add(Position(row - 1, col + 2))
        set.add(Position(row - 1, col - 2))
        set.add(Position(row + 1, col + 2))
        set.add(Position(row + 1, col - 2))

        set.filter { it.row in 0..7 && it.col in 0..7 }

        return set.filter { it.row in 0..7 && it.col in 0..7 }.toHashSet()

    }

    override fun requiresEmpty(dst: Position): Set<Position> = emptySet()
}
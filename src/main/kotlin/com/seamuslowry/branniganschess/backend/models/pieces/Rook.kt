package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.utils.Utils
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("ROOK")
class Rook(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.ROOK, color, game, positionRow, positionCol, taken, id) {
    override fun copy() = Rook(color, game, positionRow, positionCol, taken, id)
    override fun plausibleMoves(): Set<Position> {
        val (row, col) = position() ?: return emptySet()

        val set = HashSet<Position>()
        for (d in 1..7) {
            set.add(Position(row, col + d))
            set.add(Position(row, col - d))
            set.add(Position(row + d, col))
            set.add(Position(row - d, col))
        }

        return set.filter { Utils.tileOnBoard(it.row, it.col) }.toHashSet()
    }

    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        return row == dst.row || col == dst.col
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)
}
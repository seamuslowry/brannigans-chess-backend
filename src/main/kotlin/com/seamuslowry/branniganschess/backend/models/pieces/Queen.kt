package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import kotlin.math.abs
import kotlin.math.max

@Entity
@DiscriminatorValue("QUEEN")
class Queen(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.QUEEN, color, game, positionRow, positionCol, taken, id) {
    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return row == dst.row || col == dst.col || rowDiff == colDiff
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)

    override fun requiresEmpty(dst: Position): Set<Position> {
        val set = HashSet<Position>()
        val (row, col) = position() ?: return emptySet()

        val rowDiff = dst.row - row
        val colDiff = dst.col - col
        val rowDirection = if (rowDiff != 0) rowDiff / abs(rowDiff) else 0
        val colDirection = if (colDiff != 0) colDiff / abs(colDiff) else 0

        for (i in 1 until max(abs(rowDiff), abs(colDiff))) {
            set.add(Position(row + rowDirection * i, col + colDirection * i))
        }

        return set
    }
}
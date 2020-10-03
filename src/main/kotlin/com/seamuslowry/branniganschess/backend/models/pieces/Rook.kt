package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
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
    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        return row == dst.row || col == dst.col
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)
    override fun requiresEmpty(dst: Position): Set<Position> {
        val (row, col) = position() ?: return emptySet()

        if (row == dst.row) {
            val sortedCols = listOf(col, dst.col).sorted()
            return (sortedCols[0] + 1 until sortedCols[1]).map { Position(row, it) }.toHashSet()
        }

        if (col == dst.col) {
            val sortedRows = listOf(row, dst.row).sorted()
            return (sortedRows[0] + 1 until sortedRows[1]).map { Position(it, col) }.toHashSet()
        }

        return emptySet()
   }
}
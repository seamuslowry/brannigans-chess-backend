package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
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
    override fun isImmovable(): Boolean = super.isImmovable() || positionRow == 0 || positionRow == 7

    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false
        val direction = direction()

        val rowDiff = dst.row - row
        return dst.col == col && (rowDiff == direction || (row == startingRow() && rowDiff == direction * 2))
    }

    override fun canCapture(dst: Position): Boolean {
        if (!super.canCapture(dst)) return false
        val (row, col) = position() ?: return false
        val direction = direction()

        val rowDiff = dst.row - row
        val colDiff = abs(dst.col - col)

        return rowDiff == direction && colDiff == 1
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
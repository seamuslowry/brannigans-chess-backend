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
    override fun copy() = Pawn(color, game, positionRow, positionCol, taken, id)
    override fun isImmovable(): Boolean = super.isImmovable() || positionRow == 0 || positionRow == 7
    override fun plausibleMoves(): Set<Position> {
        val direction = direction()
        val (row, col) = position() ?: return emptySet()
        if (abs(row - startingRow()) == 6) return emptySet()

        val set = mutableSetOf(
                Position(row + direction, col),
                Position(row + direction, col + 1),
                Position(row + direction, col - 1)
        )

        if (row == startingRow()) set.add(Position(row + direction * 2, col))

        return set
    }

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

    private fun startingRow() = if (PieceColor.BLACK == color) 1 else 6

    private fun direction() = if (PieceColor.BLACK == color) 1 else -1
}
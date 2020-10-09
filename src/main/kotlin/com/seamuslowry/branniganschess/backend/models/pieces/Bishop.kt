package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.utils.Utils
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import kotlin.math.abs

@Entity
@DiscriminatorValue("BISHOP")
class Bishop(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        status: PieceStatus = PieceStatus.ACTIVE,
        id: Long? = null
): Piece(PieceType.BISHOP, color, game, positionRow, positionCol, status, id) {
    override fun copy() = Bishop(color, game, positionRow, positionCol, status, id)
    override fun plausibleMoves(): Set<Position> {
        val (row, col) = position() ?: return emptySet()

        val set = HashSet<Position>()
        for (d in 1..7) {
            set.add(Position(row + d, col + d))
            set.add(Position(row - d, col + d))
            set.add(Position(row + d, col - d))
            set.add(Position(row - d, col - d))
        }

        return set.filter { Utils.tileOnBoard(it.row, it.col) }.toHashSet()
    }

    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return rowDiff == colDiff
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)
}
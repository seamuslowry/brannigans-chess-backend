package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.utils.Utils
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("ROOK")
class Rook(
        color: PieceColor,
        gameId: Long,
        positionRow: Int = 0,
        positionCol: Int = 0,
        status: PieceStatus = PieceStatus.ACTIVE,
        id: Long = -1
): Piece(PieceType.ROOK, color, gameId, positionRow, positionCol, status, id) {
    override fun copy() = Rook(color, gameId, positionRow, positionCol, status, id)
    override fun plausibleMoves(): Set<Position> {
        val (row, col) = position()

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
        val (row, col) = position()

        return row == dst.row || col == dst.col
    }
}
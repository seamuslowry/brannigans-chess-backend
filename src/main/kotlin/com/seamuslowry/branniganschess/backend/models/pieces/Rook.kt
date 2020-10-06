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
    override fun copy() = Rook(color, game, positionRow, positionCol, taken, id)
    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        return row == dst.row || col == dst.col
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)
}
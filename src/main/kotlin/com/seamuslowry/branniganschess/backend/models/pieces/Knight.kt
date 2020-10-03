package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import kotlin.math.abs

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
    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return setOf(rowDiff,colDiff) == setOf(1,2)
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)
}
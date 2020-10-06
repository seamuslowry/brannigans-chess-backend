package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import kotlin.math.abs

@Entity
@DiscriminatorValue("KING")
class King(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.KING, color, game, positionRow, positionCol, taken, id) {
    override fun copy() = King(color, game, positionRow, positionCol, taken, id)
    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return setOf(0, 1).containsAll(setOf(rowDiff,colDiff))
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)

    private fun startingRow() = if (color == PieceColor.BLACK) 0 else 7

    fun kingSideCastleRequest() = Position(startingRow(), 6)
    fun kingSideCastleRook() = Position(startingRow(), 7)

    fun queenSideCastleRequest() = Position(startingRow(), 2)
    fun queenSideCastleRook() = Position(startingRow(), 0)
}
package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import kotlin.math.abs
import kotlin.math.min

@Entity
@DiscriminatorValue("BISHOP")
class Bishop(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.BISHOP, color, game, positionRow, positionCol, taken, id) {
    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return rowDiff == colDiff
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)

    override fun requiresEmpty(dst: Position): Set<Position> {
        val set = HashSet<Position>()
        val (row, col) = position() ?: return emptySet()

        // sort the rows and columns of the source and destination
        // need them sorted to create the ranges accurately
        val sortedRows = listOf(row, dst.row).sorted()
        val sortedCols = listOf(col, dst.col).sorted()

        // the ranges of rows and columns that the piece can end up in
        // for valid data, a Position made up of Position(rowRange[x],colRange[x])
        // where x is any valid index, should be a valid move for the piece
        val rowRange = sortedRows[0] until sortedRows[1]
        val colRange = sortedCols[0] until sortedCols[1]

        // create a set of all Positions for each index as described above
        // take the min of the range to account for in invalid destination being passed
        for (i in 1 until min(rowRange.count(), colRange.count())) {
            set.add(Position(rowRange.elementAt(i),rowRange.elementAt(i)))
        }

        return set
    }
}
package com.seamuslowry.branniganschess.backend.models.pieces

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.utils.Utils
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
    override fun copy() = Knight(color, game, positionRow, positionCol, taken, id)
    override fun plausibleMoves(): Set<Position> {
        val (row, col) = position() ?: return emptySet()

        val set = HashSet<Position>()
        set.add(Position(row + 2, col + 1))
        set.add(Position(row + 2, col - 1))
        set.add(Position(row - 2 , col + 1))
        set.add(Position(row - 2, col - 1))
        set.add(Position(row + 1, col + 2))
        set.add(Position(row + 1, col - 2))
        set.add(Position(row - 1 , col + 2))
        set.add(Position(row - 1, col - 2))

        return set.filter { Utils.tileOnBoard(it.row, it.col) }.toHashSet()
    }

    override fun canMove(dst: Position): Boolean {
        if (!super.canMove(dst)) return false
        val (row, col) = position() ?: return false

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return setOf(rowDiff,colDiff) == setOf(1,2)
    }

    override fun canCapture(dst: Position): Boolean = canMove(dst)

    override fun requiresEmpty(dst: Position): Set<Position> = emptySet()
}
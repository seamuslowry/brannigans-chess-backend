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
        gameId: Long,
        positionRow: Int = 0,
        positionCol: Int = 0,
        status: PieceStatus = PieceStatus.ACTIVE,
        id: Long = -1
): Piece(PieceType.KNIGHT, color, gameId, positionRow, positionCol, status, id) {
    override fun copy() = Knight(color, gameId, positionRow, positionCol, status, id)
    override fun plausibleMoves(): Set<Position> {
        val (row, col) = position()

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
        val (row, col) = position()

        val rowDiff = abs(dst.row - row)
        val colDiff = abs(dst.col - col)

        return setOf(rowDiff,colDiff) == setOf(1,2)
    }

    override fun requiresEmpty(dst: Position): Set<Position> = emptySet()
}
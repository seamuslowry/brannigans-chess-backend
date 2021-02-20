package com.seamuslowry.branniganschess.backend.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.utils.Utils
import javax.persistence.*
import kotlin.math.abs
import kotlin.math.max

@Entity
@Inheritance
@DiscriminatorColumn(
        discriminatorType = DiscriminatorType.STRING,
        name = "type"
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(Knight::class),
        JsonSubTypes.Type(Queen::class),
        JsonSubTypes.Type(Rook::class),
        JsonSubTypes.Type(King::class),
        JsonSubTypes.Type(Bishop::class),
        JsonSubTypes.Type(Pawn::class)
)
abstract class Piece (
        @Enumerated(EnumType.STRING)
        @Column(name="type", insertable = false, updatable = false)
        open val type: PieceType,
        @Enumerated(EnumType.STRING)
        open val color: PieceColor,
        open val gameId: Long,
        open var positionRow: Int,
        open var positionCol: Int,
        @Enumerated(EnumType.STRING)
        open var status: PieceStatus,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        open var id: Long
) {
    // need copy but cannot use data class
    abstract fun copy(): Piece
    abstract fun plausibleMoves(): Set<Position>
    open fun isImmovable() = status != PieceStatus.ACTIVE
    open fun position() = Position(positionRow, positionCol)
    open fun canMove(dst: Position) = !isImmovable() && Utils.tileOnBoard(dst.row, dst.col)
    open fun canCapture(dst: Position) = canMove(dst)
    open fun requiresEmpty(dst: Position): Set<Position> {
        val set = HashSet<Position>()
        val (row, col) = position()

        val rowDiff = dst.row - row
        val colDiff = dst.col - col
        val rowDirection = if (rowDiff != 0) rowDiff / abs(rowDiff) else 0
        val colDirection = if (colDiff != 0) colDiff / abs(colDiff) else 0

        for (i in 1 until max(abs(rowDiff), abs(colDiff))) {
            set.add(Position(row + rowDirection * i, col + colDirection * i))
        }

        return set
    }
}
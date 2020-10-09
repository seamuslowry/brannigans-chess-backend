package com.seamuslowry.branniganschess.backend.models

import com.fasterxml.jackson.annotation.JsonIgnore
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
        @ManyToOne
        @JsonIgnore
        // nullable because of JsonIgnore
        // do NOT want to send it up to the client
        // cannot be saved as null
        open val game: Game?,
        open var positionRow: Int? = null,
        open var positionCol: Int? = null,
        open var status: PieceStatus = PieceStatus.ACTIVE,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        open var id: Long? = null
) {
    // need copy but cannot use data class
    abstract fun copy(): Piece
    abstract fun plausibleMoves(): Set<Position>
    open fun isImmovable() = positionCol == null || positionRow == null || status != PieceStatus.ACTIVE
    open fun position() = positionRow?.let {row ->
        positionCol?.let {col ->
            Position(row, col)
        }
    }
    open fun canMove(dst: Position) = !isImmovable() && Utils.tileOnBoard(dst.row, dst.col)
    open fun canCapture(dst: Position) = !isImmovable() && Utils.tileOnBoard(dst.row, dst.col)
    open fun requiresEmpty(dst: Position): Set<Position> {
        val set = HashSet<Position>()
        val (row, col) = position() ?: return emptySet()

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
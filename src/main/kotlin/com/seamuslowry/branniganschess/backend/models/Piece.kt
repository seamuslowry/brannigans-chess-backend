package com.seamuslowry.branniganschess.backend.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.utils.Utils
import javax.persistence.*

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
    open var taken: Boolean = false,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
) {
    open fun isImmovable() = positionCol == null || positionRow == null || taken
    open fun position() = positionRow?.let {row ->
        positionCol?.let {col ->
            Position(row, col)
        }
    }
    open fun canMove(dst: Position) = !isImmovable() && Utils.tileOnBoard(dst.row, dst.col)
    open fun canCapture(dst: Position) = !isImmovable() && Utils.tileOnBoard(dst.row, dst.col)
    open fun requiresEmpty(dst: Position): Set<Position> = emptySet()
}
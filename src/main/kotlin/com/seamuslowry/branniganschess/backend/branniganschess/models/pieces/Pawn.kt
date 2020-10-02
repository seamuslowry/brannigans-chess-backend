package com.seamuslowry.branniganschess.backend.branniganschess.models.pieces

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("PAWN")
class Pawn(
        color: PieceColor,
        game: Game?,
        positionRow: Int? = null,
        positionCol: Int? = null,
        taken: Boolean = false,
        id: Long? = null
): Piece(PieceType.PAWN, color, game, positionRow, positionCol, taken, id) {
    override fun plausibleCaptures(): Set<Position> {
        return HashSet()
    }

    override fun plausibleMoves(): Set<Position> {
        return HashSet()
    }
}
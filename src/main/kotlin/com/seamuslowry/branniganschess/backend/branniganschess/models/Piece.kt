package com.seamuslowry.branniganschess.backend.branniganschess.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Piece (
    @Enumerated(EnumType.ORDINAL)
    val type: PieceType,
    @Enumerated(EnumType.STRING)
    val color: PieceColor,
    @ManyToOne
    @JsonIgnore
    val game: Game,
    var positionRow: Int? = null,
    var positionCol: Int? = null,
    var taken: Boolean = false,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {}
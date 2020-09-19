package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.*

@Entity
class Piece (
    @Enumerated(EnumType.ORDINAL)
    val type: PieceType,
    @Enumerated(EnumType.STRING)
    val color: PieceColor,
    @ManyToOne
    val game: Game,
    var row: Int? = null,
    var col: Int? = null,
    var taken: Boolean = false,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {}
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
    // nullable because of JsonIgnore
    // do NOT want to send it up to the client
    // cannot be saved as null
    val game: Game?,
    var positionRow: Int? = null,
    var positionCol: Int? = null,
    var taken: Boolean = false,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) {}
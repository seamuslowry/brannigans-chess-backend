package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class Move {
    @Id
    @GeneratedValue
    private var id: Long? = null

    private val fromX: Number? = null

    private val fromY: Number? = null

    private val toX: Number? = null

    private val toY: Number? = null

    @ManyToOne
    private val movingPiece: Piece? = null

    @ManyToOne
    private val takenPiece: Piece? = null
}
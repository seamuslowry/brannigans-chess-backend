package com.seamuslowry.branniganschess.backend.models

import javax.persistence.*

@Entity
class Move  (
        @ManyToOne
        val movingPiece: Piece,
        val srcRow: Int,
        val srcCol: Int,
        val dstRow: Int,
        val dstCol: Int,
        @ManyToOne
        val takenPiece: Piece? = null,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1
){
}
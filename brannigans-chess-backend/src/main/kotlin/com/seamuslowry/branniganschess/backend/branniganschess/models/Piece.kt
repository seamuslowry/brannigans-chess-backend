package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.*


enum class PieceType {
    KNIGHT,
    BISHOP,
    PAWN,
    ROOK,
    QUEEN,
    KING
}

enum class PieceColor {
    BLACK,
    WHITE
}

@MappedSuperclass
abstract class Piece {
    @Id
    @GeneratedValue
    private var id: Long? = null

    @Enumerated(EnumType.ORDINAL)
    private val type: PieceType = PieceType.QUEEN;

    @Enumerated(EnumType.STRING)
    private val color: PieceColor = PieceColor.WHITE;

    private var taken: Boolean = false;

    private var posX: Number? = null;

    private var posY: Number? = null;
}
package com.seamuslowry.branniganschess.backend.dtos

data class PieceIdentifierDto(
        val gameId: Long,
        val row: Int,
        val col: Int
)
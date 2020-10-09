package com.seamuslowry.branniganschess.backend.dtos

import com.seamuslowry.branniganschess.backend.models.PieceType

data class PromotionRequest (
        val row: Int,
        val col: Int,
        val type: PieceType
)
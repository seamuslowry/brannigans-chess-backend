package com.seamuslowry.branniganschess.backend.utils

import com.seamuslowry.branniganschess.backend.models.Piece

object Utils {
    fun getEmptyBoard() = Array(8) {
        arrayOfNulls<Piece?>(8)
    }
}
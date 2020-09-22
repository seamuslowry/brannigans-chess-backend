package com.seamuslowry.branniganschess.backend.branniganschess.utils

import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece

object Utils {
    fun getEmptyBoard() = Array(8) {
        arrayOfNulls<Piece?>(8)
    }
}
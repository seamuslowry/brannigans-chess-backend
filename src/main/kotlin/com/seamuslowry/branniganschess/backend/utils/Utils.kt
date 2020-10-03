package com.seamuslowry.branniganschess.backend.utils

import com.seamuslowry.branniganschess.backend.models.Piece

object Utils {
    fun getEmptyBoard() = Array(8) {
        arrayOfNulls<Piece?>(8)
    }

    fun tileOnBoard(row: Int, col: Int): Boolean {
        return row in 0..7 && col in 0..7
    }
}
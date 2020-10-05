package com.seamuslowry.branniganschess.backend.utils

import com.seamuslowry.branniganschess.backend.models.Piece
import com.seamuslowry.branniganschess.backend.models.PieceColor

object Utils {
    fun getEmptyBoard() = Array(8) {
        arrayOfNulls<Piece?>(8)
    }

    fun tileOnBoard(row: Int, col: Int): Boolean {
        return row in 0..7 && col in 0..7
    }

    fun copyBoard(board: Array<Array<Piece?>>): Array<Array<Piece?>> = board.map {
        it.clone()
    }.toTypedArray()

    fun getOpposingColor(color: PieceColor): PieceColor = if (color == PieceColor.BLACK) PieceColor.WHITE else PieceColor.BLACK
}
package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceType
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PieceService (
        private val pieceRepository: PieceRepository
) {
    fun createPiece(p: Piece): Piece = pieceRepository.save(p)
}
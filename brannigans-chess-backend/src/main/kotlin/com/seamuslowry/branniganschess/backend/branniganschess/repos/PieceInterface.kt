package com.seamuslowry.branniganschess.backend.branniganschess.repos

import com.seamuslowry.branniganschess.backend.branniganschess.models.Move
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import org.springframework.data.jpa.repository.JpaRepository

interface PieceInterface: JpaRepository<Piece, Long> {
}
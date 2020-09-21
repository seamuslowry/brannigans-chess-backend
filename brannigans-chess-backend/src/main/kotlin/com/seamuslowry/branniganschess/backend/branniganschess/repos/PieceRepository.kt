package com.seamuslowry.branniganschess.backend.branniganschess.repos

import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface PieceRepository: JpaRepository<Piece, Long>, JpaSpecificationExecutor<Piece> {
}
package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Move
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class MoveService (
        private val moveRepository: MoveRepository
) {
    fun createMove(move: Move): Move = moveRepository.save(move)

    fun findAllBy(gameId: Long, color: PieceColor? = null): Iterable<Move> {
        var spec: Specification<Move> = Specification.where(inGame(gameId))!!

        color?.let { spec = spec.and(fromColor(it))!! }

        return moveRepository.findAll(spec)
    }

    private fun inGame(id: Long): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Piece>("movingPiece").get<Game>("game").get<Long>("id"), id)
    }

    private fun fromColor(color: PieceColor): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Piece>("movingPiece").get<PieceColor>("color"), color)
    }
}
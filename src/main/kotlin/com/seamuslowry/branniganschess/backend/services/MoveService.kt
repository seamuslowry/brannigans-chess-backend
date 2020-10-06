package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.models.Piece
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.repos.MoveRepository
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

    fun findLastMove(gameId: Long): Move? {
        val allMoves = findAllBy(gameId)
        return if (allMoves.count() > 0) allMoves.last() else null
    }

    fun hasMoved(piece: Piece): Boolean = moveRepository.findAll(Specification.where(fromPiece(piece))).isNotEmpty()

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

    private fun fromPiece(piece: Piece): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Piece>("movingPiece").get<Long>("id"), piece.id)
    }
}
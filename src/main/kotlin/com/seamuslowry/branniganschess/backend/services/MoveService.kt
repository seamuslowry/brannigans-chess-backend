package com.seamuslowry.branniganschess.backend.services

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
    /**
     * Create the provided move.
     *
     * @param move the move to save
     *
     * @return the saved move
     */
    fun createMove(move: Move): Move = moveRepository.save(move)

    /**
     * Find all moves that match the given criteria.
     *
     * @param gameId the id move's game
     * @param colors the colors any involved piece should be
     *
     * @return a list of matching moves
     */
    fun findAllBy(gameId: Long, colors: Iterable<PieceColor> = listOf(PieceColor.BLACK, PieceColor.WHITE)): Iterable<Move> {
        var spec: Specification<Move> = Specification.where(inGame(gameId))!!

        var colorsSpec: Specification<Move> = Specification.where(fromColor(null))!!
        colors.forEach { colorsSpec = colorsSpec.or(fromColor(it).or(isTake()))!! }

        spec = spec.and(colorsSpec)!!
        return moveRepository.findAll(spec)
    }

    /**
     * Find all moves that match the given criteria.
     *
     * @param gameId the id move's game
     * @param color the color of the piece that moved
     *
     * @return a list of matching moves
     */
    fun findAllBy(gameId: Long, color: PieceColor): Iterable<Move> = findAllBy(gameId, listOf(color))

    /**
     * Find all moved that are shared between players in a game
     *
     * @param gameId the id move's game
     *
     * @return a list of shared moves
     */
    fun findSharedMoves(gameId: Long): Iterable<Move> {
        val spec: Specification<Move> = Specification.where(inGame(gameId).and((isTake())))!!
        return moveRepository.findAll(spec)
    }

    /**
     * Find the most recent move of the provided game.
     *
     * @param gameId the id of the game to get the move from
     *
     * @return the most recent move; null if there are no moves
     */
    fun findLastMove(gameId: Long): Move? {
        val allMoves = findAllBy(gameId)
        return if (allMoves.count() > 0) allMoves.last() else null
    }

    /**
     * Return whether the provided piece has moved already.
     *
     * @param piece the piece to check moves for
     *
     * @return true if the piece has moved already; false otherwise
     */
    fun hasMoved(piece: Piece): Boolean = moveRepository.findAll(Specification.where(fromPiece(piece))).isNotEmpty()

    private fun inGame(id: Long): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Piece>("movingPiece").get<Long>("gameId"), id)
    }

    private fun fromColor(color: PieceColor?): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Piece>("movingPiece").get<PieceColor>("color"), color)
    }

    private fun isTake(): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.isNotNull(root.get<Piece>("takenPiece"))
    }

    private fun fromPiece(piece: Piece): Specification<Move> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Piece>("movingPiece").get<Long>("id"), piece.id)
    }
}
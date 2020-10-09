package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.utils.Utils
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class PieceService (
        private val pieceRepository: PieceRepository
) {
    fun createPiece(p: Piece): Piece = pieceRepository.save(p)

    fun updatePiece(p: Piece): Piece = pieceRepository.save(p)

    fun takePiece(p: Piece): Piece {
        p.status = PieceStatus.TAKEN
        p.positionRow = null
        p.positionCol = null
        return updatePiece(p)
    }

    fun movePiece(p: Piece, dstRow: Int, dstCol: Int): Piece {
        p.positionCol = dstCol
        p.positionRow = dstRow
        return updatePiece(p)
    }

    fun findAllBy(gameId: Long, color: PieceColor? = null, status: PieceStatus? = null, type: PieceType? = null): Iterable<Piece> {
        var spec: Specification<Piece> = Specification.where(inGame(gameId))!!

        color?.let { spec = spec.and(isColor(it))!! }
        status?.let { spec = spec.and(isStatus(it))!! }
        type?.let { spec = spec.and(isType(it))!! }

        return pieceRepository.findAll(spec)
    }

    fun getPiecesAsBoard(gameId: Long): Array<Array<Piece?>> {
        val activePieces = findAllBy(gameId, status = PieceStatus.ACTIVE)

        val array: Array<Array<Piece?>> = Utils.getEmptyBoard()

        for (piece in activePieces) {
            array[piece.positionRow ?: continue][piece.positionCol ?: continue] = piece
        }

        return array
    }

    fun getPieceAt(gameId: Long, row: Int, col: Int): Piece? {
        val spec: Specification<Piece> = Specification
                .where(inGame(gameId))!!
                .and(inRow(row))!!
                .and(inCol(col))!!

        return pieceRepository.findAll(spec).firstOrNull()
    }

    private fun inGame(id: Long): Specification<Piece> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Game>("game").get<Long>("id"), id)
    }

    private fun isColor(color: PieceColor): Specification<Piece> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<PieceColor>("color"), color)
    }

    private fun isType(type: PieceType): Specification<Piece> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<PieceType>("type"), type)
    }

    private fun isStatus(status: PieceStatus): Specification<Piece> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<PieceStatus>("status"), status)
    }

    private fun inRow(row: Int): Specification<Piece> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Int>("positionRow"), row)
    }

    private fun inCol(col: Int): Specification<Piece> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.equal(root.get<Int>("positionCol"), col)
    }
}
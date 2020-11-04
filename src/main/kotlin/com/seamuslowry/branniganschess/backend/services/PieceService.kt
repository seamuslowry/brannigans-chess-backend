package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.PieceIdentifierDto
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.utils.Utils
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class PieceService (
        private val pieceRepository: PieceRepository
) {
    /**
     * Create the provided piece.
     *
     * @param p the piece to create
     *
     * @return the saved piece
     */
    fun createPiece(p: Piece): Piece = pieceRepository.save(p)

    /**
     * Update the provided piece.
     *
     * @param p the piece to update
     *
     * @return the saved piece
     */
    fun updatePiece(p: Piece): Piece = pieceRepository.save(p)

    /**
     * Take the provided piece.
     *
     * @param p the piece to take
     *
     * @return the taken piece
     */
    fun takePiece(p: Piece): Piece {
        p.status = PieceStatus.TAKEN
        return updatePiece(p)
    }

    /**
     * Remove the provided piece. Removed pieces are considered no longer a part of the game.
     * They are not considered "taken" in the sense that they should be shown in the taken section of the client.
     * Pawns that has been promoted to another piece are REMOVED.
     *
     * @param p the piece to remove
     *
     * @return the removed piece
     */
    fun removePiece(p: Piece): Piece {
        p.status = PieceStatus.REMOVED
        return updatePiece(p)
    }

    /**
     * Move the provided piece to the given location
     *
     * @param p the piece to move
     * @param dstRow the row to move to
     * @param dstCol the col to move to
     *
     * @return the moved piece
     */
    fun movePiece(p: Piece, dstRow: Int, dstCol: Int): Piece {
        p.positionCol = dstCol
        p.positionRow = dstRow
        return updatePiece(p)
    }

    /**
     * Find all the pieces that meet the given criteria.
     *
     * @param gameId the id of the piece's game
     * @param color the color of the piece
     * @param status the status of the piece
     * @param type the type of the piece
     *
     * @return the list of matching pieces
     */
    fun findAllBy(gameId: Long, color: PieceColor? = null, status: PieceStatus? = null, type: PieceType? = null): Iterable<Piece> {
        var spec: Specification<Piece> = Specification.where(inGame(gameId))!!

        color?.let { spec = spec.and(isColor(it))!! }
        status?.let { spec = spec.and(isStatus(it))!! }
        type?.let { spec = spec.and(isType(it))!! }

        return pieceRepository.findAll(spec)
    }

    /**
     * Get the active pieces in a game as a 2D array denoting the board.
     *
     * @param gameId the id of the game to retrieve pieces for
     *
     * @return a 2D array of the active pieces as a board
     */
    fun getPiecesAsBoard(gameId: Long): Array<Array<Piece?>> {
        val activePieces = findAllBy(gameId, status = PieceStatus.ACTIVE)

        val array: Array<Array<Piece?>> = Utils.getEmptyBoard()

        for (piece in activePieces) {
            array[piece.positionRow][piece.positionCol] = piece
        }

        return array
    }

    /**
     * Get a piece currently at the given row and col
     *
     * @param gameId the id of the piece's game
     * @param row the row to search
     * @param col the col to search
     *
     * @return the piece at that location; null if no piece is there
     */
    fun getPieceAt(gameId: Long, row: Int, col: Int): Piece? {
        val spec: Specification<Piece> = Specification
                .where(inGame(gameId))!!
                .and(inRow(row))!!
                .and(inCol(col))!!
                .and(isStatus(PieceStatus.ACTIVE))!!

        return pieceRepository.findAll(spec).firstOrNull()
    }

    /**
     * Promote the piece identified to the given type.
     *
     * @param pieceIdentifierDto a [PieceIdentifierDto] that uniquely identifies the piece that should be promoted
     * @param type the type of piece that the identified piece should be promoted to
     *
     * @return the new piece
     *
     * @throws [ChessRuleException] when the promotion request is not valid
     */
    fun promote(pieceIdentifierDto: PieceIdentifierDto, type: PieceType): Piece {
        val (gameId, row, col) = pieceIdentifierDto
        val p = getPieceAt(gameId, row, col)
        if (p is Pawn && p.promotable()) {
            val piece = createPiece(when(type) {
                PieceType.QUEEN -> Queen(p.color, p.game, p.positionRow, p.positionCol)
                PieceType.ROOK -> Rook(p.color, p.game, p.positionRow, p.positionCol)
                PieceType.BISHOP -> Bishop(p.color, p.game, p.positionRow, p.positionCol)
                PieceType.KNIGHT -> Knight(p.color, p.game, p.positionRow, p.positionCol)
                else -> throw ChessRuleException("Kif, you have to follow the rules. I don't, but you do.")
            })
            removePiece(p)
            return piece
        }

        throw ChessRuleException("Kif, I just... I don't even know...")
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
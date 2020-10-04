package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.utils.Utils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.abs

@Service
class GameService (
        private val gameRepository: GameRepository,
        private val pieceService: PieceService,
        private val moveService: MoveService
) {
    fun createGame(): Game {
        val newGame = gameRepository.save(Game(UUID.randomUUID().toString()))

        // create all the pieces
        // black side
        pieceService.createPiece(Rook(PieceColor.BLACK, newGame, 0, 0))
        pieceService.createPiece(Knight(PieceColor.BLACK, newGame, 0, 1))
        pieceService.createPiece(Bishop(PieceColor.BLACK, newGame, 0, 2))
        pieceService.createPiece(Queen(PieceColor.BLACK, newGame, 0, 3))
        pieceService.createPiece(King(PieceColor.BLACK, newGame, 0, 4))
        pieceService.createPiece(Bishop(PieceColor.BLACK, newGame, 0, 5))
        pieceService.createPiece(Knight(PieceColor.BLACK, newGame, 0, 6))
        pieceService.createPiece(Rook(PieceColor.BLACK, newGame, 0, 7))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 0))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 1))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 2))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 3))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 4))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 5))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 6))
        pieceService.createPiece(Pawn(PieceColor.BLACK, newGame, 1, 7))

        // white side
        pieceService.createPiece(Rook(PieceColor.WHITE, newGame, 7, 0))
        pieceService.createPiece(Knight(PieceColor.WHITE, newGame, 7, 1))
        pieceService.createPiece(Bishop(PieceColor.WHITE, newGame, 7, 2))
        pieceService.createPiece(Queen(PieceColor.WHITE, newGame, 7, 3))
        pieceService.createPiece(King(PieceColor.WHITE, newGame, 7, 4))
        pieceService.createPiece(Bishop(PieceColor.WHITE, newGame, 7, 5))
        pieceService.createPiece(Knight(PieceColor.WHITE, newGame, 7, 6))
        pieceService.createPiece(Rook(PieceColor.WHITE, newGame, 7, 7))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 0))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 1))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 2))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 3))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 4))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 5))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 6))
        pieceService.createPiece(Pawn(PieceColor.WHITE, newGame, 6, 7))

        return newGame
    }

    fun findAllBy(active: Boolean?, pageable: Pageable): Page<Game> {
        var spec: Specification<Game> = Specification.where(null)!!

        active?.let { spec = if (active) spec.and(isActive())!! else spec.and(isWon())!! }

        return gameRepository.findAll(spec, pageable)
    }

    fun move(gameId: Long, moveRequest: MoveRequest): Move {
        val activePieces = pieceService.getPiecesAsBoard(gameId)
        val (srcRow, srcCol, dstRow, dstCol) = moveRequest

        if (srcRow == dstRow && srcCol == dstCol) throw ChessRuleException("Kif, you fool! You're moving a piece right back where it was!")
        if (!Utils.tileOnBoard(srcRow, srcCol)) throw ChessRuleException("Kif, what have I told you about reaching for pieces off the board?")
        if (!Utils.tileOnBoard(dstRow, dstCol)) throw ChessRuleException("Kif, if you'd like to move a piece off the board, you should just give up.")

        val movingPiece = activePieces[srcRow][srcCol] ?: throw ChessRuleException("Kif, what have I told you about moving a piece from an empty tile?")

        var move = tryEnPassant(activePieces, movingPiece, moveRequest)

        move = move ?: tryStandardMove(activePieces, movingPiece, moveRequest)

        return applyMove(move)
    }

    private fun tryEnPassant(board: Array<Array<Piece?>>, movingPiece: Piece, moveRequest: MoveRequest): Move? {
        if (movingPiece is Pawn) {
            val (srcRow, srcCol, dstRow, dstCol) = moveRequest

            val passantTarget = board[srcRow][dstCol]
            if (!movingPiece.canCapture(Position(dstRow, dstCol))) return null
            if (passantTarget == null) return null
            if (passantTarget !is Pawn) return null
            if (passantTarget.color == movingPiece.color) return null

            val lastMove = moveService.findLastMove(movingPiece.game?.id ?: 0)
            if (lastMove?.movingPiece?.id != passantTarget.id) return null
            if (lastMove?.dstRow?.minus(lastMove.srcRow)?.let { abs(it) } != 2) return null

            return Move(
                    movingPiece,
                    srcRow,
                    srcCol,
                    dstRow,
                    dstCol,
                    passantTarget,
                    MoveType.EN_PASSANT
            )
        }

        return null
    }

    private fun tryStandardMove(board: Array<Array<Piece?>>, movingPiece: Piece, moveRequest: MoveRequest): Move {
        val (srcRow, srcCol, dstRow, dstCol) = moveRequest
        val targetPiece = board[dstRow][dstCol]
        val dst = Position(dstRow, dstCol)
        val plausibleMove = if (targetPiece === null) movingPiece.canMove(dst) else movingPiece.canCapture(dst)
        if (!plausibleMove) throw ChessRuleException("Kiff, I don't think that piece moves like that.")

        val requiredEmpty = movingPiece.requiresEmpty(dst)
        if(requiredEmpty.any { board[it.row][it.col] != null }) throw ChessRuleException("Kiff, that piece is being blocked by another.")

        if (targetPiece?.color == movingPiece.color) throw ChessRuleException("Kiff, if I can't kill my own men anymore, neither can you.")

        return Move(
                movingPiece,
                srcRow,
                srcCol,
                dstRow,
                dstCol,
                targetPiece
        )
    }

    private fun applyMove(move: Move): Move {
        val takenPiece = move.takenPiece?.let {
            pieceService.takePiece(it)
        }
        val savedMovingPiece = pieceService.movePiece(move.movingPiece, move.dstRow, move.dstCol)

        return moveService.createMove(Move(
                savedMovingPiece,
                move.srcRow,
                move.srcCol,
                move.dstRow,
                move.dstCol,
                takenPiece,
                move.moveType
        ))
    }

    private fun isActive(): Specification<Game> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.isNull(root.get<Player>("winner"))
    }

    private fun isWon(): Specification<Game> = Specification {
        root,
        _,
        criteriaBuilder -> criteriaBuilder.isNotNull(root.get<Player>("winner"))
    }
}
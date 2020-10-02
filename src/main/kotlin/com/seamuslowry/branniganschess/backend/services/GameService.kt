package com.seamuslowry.branniganschess.backend.services

import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*

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

        if (srcRow == dstRow && srcCol == dstCol) throw ChessRuleException("Kiff, you fool! You're moving a piece right back where it was!")
        if (!tileOnBoard(srcRow, srcCol)) throw ChessRuleException("Kiff, what have I told you about reaching for pieces off the board?")
        if (!tileOnBoard(dstRow, dstCol)) throw ChessRuleException("Kiff, if you'd like to move a piece off the board, you should just give up.")

        var movingPiece = activePieces[srcRow][srcCol] ?: throw ChessRuleException("Kiff, what have I told you about moving a piece from an empty tile?")
        var targetPiece = activePieces[dstRow][dstCol]

        if (targetPiece?.color == movingPiece.color) throw ChessRuleException("Kiff, if I can't kill my own men anymore, neither can you.")

        val dst = Position(dstRow, dstCol)
        val plausibleMove = if (targetPiece === null) movingPiece.canMove(dst) else movingPiece.canCapture(dst)
        if (!plausibleMove) throw ChessRuleException("Kiff, I don't think that piece moves like that.")

        val requiredEmpty = movingPiece.requiresEmpty(dst)
        if(requiredEmpty.any { activePieces[it.row][it.col] != null }) throw ChessRuleException("Kiff, that piece is being blocked by another.")

        targetPiece = targetPiece?.let {
            pieceService.takePiece(it)
        }
        movingPiece = pieceService.movePiece(movingPiece, dstRow, dstCol)

        return moveService.createMove(Move(
                movingPiece,
                srcRow,
                srcCol,
                dstRow,
                dstCol,
                targetPiece
        ))
    }

    private fun tileOnBoard(row: Int, col: Int): Boolean {
        return row in 0..7 && col in 0..7
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
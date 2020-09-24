package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.seamuslowry.branniganschess.backend.branniganschess.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.branniganschess.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
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
        // white side
        pieceService.createPiece(Piece(PieceType.ROOK, PieceColor.WHITE, newGame, 0, 0))
        pieceService.createPiece(Piece(PieceType.KNIGHT, PieceColor.WHITE, newGame, 0, 1))
        pieceService.createPiece(Piece(PieceType.BISHOP, PieceColor.WHITE, newGame, 0, 2))
        pieceService.createPiece(Piece(PieceType.QUEEN, PieceColor.WHITE, newGame, 0, 3))
        pieceService.createPiece(Piece(PieceType.KING, PieceColor.WHITE, newGame, 0, 4))
        pieceService.createPiece(Piece(PieceType.BISHOP, PieceColor.WHITE, newGame, 0, 5))
        pieceService.createPiece(Piece(PieceType.KNIGHT, PieceColor.WHITE, newGame, 0, 6))
        pieceService.createPiece(Piece(PieceType.ROOK, PieceColor.WHITE, newGame, 0, 7))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 0))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 1))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 2))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 3))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 4))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 5))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 6))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 7))

        // black side
        pieceService.createPiece(Piece(PieceType.ROOK, PieceColor.BLACK, newGame, 7, 0))
        pieceService.createPiece(Piece(PieceType.KNIGHT, PieceColor.BLACK, newGame, 7, 1))
        pieceService.createPiece(Piece(PieceType.BISHOP, PieceColor.BLACK, newGame, 7, 2))
        pieceService.createPiece(Piece(PieceType.QUEEN, PieceColor.BLACK, newGame, 7, 3))
        pieceService.createPiece(Piece(PieceType.KING, PieceColor.BLACK, newGame, 7, 4))
        pieceService.createPiece(Piece(PieceType.BISHOP, PieceColor.BLACK, newGame, 7, 5))
        pieceService.createPiece(Piece(PieceType.KNIGHT, PieceColor.BLACK, newGame, 7, 6))
        pieceService.createPiece(Piece(PieceType.ROOK, PieceColor.BLACK, newGame, 7, 7))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 0))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 1))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 2))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 3))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 4))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 5))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 6))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 6, 7))

        return newGame
    }

    fun move(gameId: Long, moveRequest: MoveRequest): Move {
        val activePieces = pieceService.getPiecesAsBoard(gameId);
        val (srcRow, srcCol, dstRow, dstCol) = moveRequest

        if (srcRow == dstRow && srcCol == dstCol) throw ChessRuleException("Kiff, you fool! You're moving a piece right back where it was!")
        if (!tileOnBoard(srcRow, srcCol)) throw ChessRuleException("Kiff, what have I told you about reaching for pieces off the board?")
        if (!tileOnBoard(dstRow, dstCol)) throw ChessRuleException("Kiff, if you'd like to move a piece off the board, you should just give up.")

        var movingPiece = activePieces[srcRow][srcCol] ?: throw ChessRuleException("Kiff, what have I told you about moving a piece from an empty tile?")
        var targetPiece = activePieces[dstRow][dstCol]

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
}
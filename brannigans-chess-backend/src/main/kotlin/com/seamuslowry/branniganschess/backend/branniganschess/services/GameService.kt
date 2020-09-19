package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceType
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameService (
        private val gameRepository: GameRepository,
        private val pieceService: PieceService
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
        pieceService.createPiece(Piece(PieceType.ROOK, PieceColor.BLACK, newGame, 8, 0))
        pieceService.createPiece(Piece(PieceType.KNIGHT, PieceColor.BLACK, newGame, 8, 1))
        pieceService.createPiece(Piece(PieceType.BISHOP, PieceColor.BLACK, newGame, 8, 2))
        pieceService.createPiece(Piece(PieceType.QUEEN, PieceColor.BLACK, newGame, 8, 3))
        pieceService.createPiece(Piece(PieceType.KING, PieceColor.BLACK, newGame, 8, 4))
        pieceService.createPiece(Piece(PieceType.BISHOP, PieceColor.BLACK, newGame, 8, 5))
        pieceService.createPiece(Piece(PieceType.KNIGHT, PieceColor.BLACK, newGame, 8, 6))
        pieceService.createPiece(Piece(PieceType.ROOK, PieceColor.BLACK, newGame, 8, 7))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 0))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 1))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 2))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 3))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 4))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 5))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 6))
        pieceService.createPiece(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 7))

        return newGame
    }
}
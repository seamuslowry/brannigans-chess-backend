package com.seamuslowry.branniganschess.backend.branniganschess

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceType
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameService (
        private val gameRepository: GameRepository,
        private val pieceRepository: PieceRepository
) {
    fun createGame(): Game {
        val newGame = gameRepository.save(Game(UUID.randomUUID().toString()))

        // create all the pieces
        // TODO refactor to use piece service to save
        // white side
        pieceRepository.save(Piece(PieceType.ROOK, PieceColor.WHITE, newGame, 0, 0))
        pieceRepository.save(Piece(PieceType.KNIGHT, PieceColor.WHITE, newGame, 0, 1))
        pieceRepository.save(Piece(PieceType.BISHOP, PieceColor.WHITE, newGame, 0, 2))
        pieceRepository.save(Piece(PieceType.QUEEN, PieceColor.WHITE, newGame, 0, 3))
        pieceRepository.save(Piece(PieceType.KING, PieceColor.WHITE, newGame, 0, 4))
        pieceRepository.save(Piece(PieceType.BISHOP, PieceColor.WHITE, newGame, 0, 5))
        pieceRepository.save(Piece(PieceType.KNIGHT, PieceColor.WHITE, newGame, 0, 6))
        pieceRepository.save(Piece(PieceType.ROOK, PieceColor.WHITE, newGame, 0, 7))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 0))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 1))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 2))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 3))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 4))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 5))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 6))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.WHITE, newGame, 1, 7))

        // black side
        pieceRepository.save(Piece(PieceType.ROOK, PieceColor.BLACK, newGame, 8, 0))
        pieceRepository.save(Piece(PieceType.KNIGHT, PieceColor.BLACK, newGame, 8, 1))
        pieceRepository.save(Piece(PieceType.BISHOP, PieceColor.BLACK, newGame, 8, 2))
        pieceRepository.save(Piece(PieceType.QUEEN, PieceColor.BLACK, newGame, 8, 3))
        pieceRepository.save(Piece(PieceType.KING, PieceColor.BLACK, newGame, 8, 4))
        pieceRepository.save(Piece(PieceType.BISHOP, PieceColor.BLACK, newGame, 8, 5))
        pieceRepository.save(Piece(PieceType.KNIGHT, PieceColor.BLACK, newGame, 8, 6))
        pieceRepository.save(Piece(PieceType.ROOK, PieceColor.BLACK, newGame, 8, 7))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 0))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 1))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 2))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 3))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 4))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 5))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 6))
        pieceRepository.save(Piece(PieceType.PAWN, PieceColor.BLACK, newGame, 7, 7))

        return newGame
    }
}
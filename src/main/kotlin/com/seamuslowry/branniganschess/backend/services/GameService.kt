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
        val game = gameRepository.getOne(gameId)
        return move(game, moveRequest)
    }

    private fun move(game: Game, moveRequest: MoveRequest): Move {
        val board = pieceService.getPiecesAsBoard(game.id)
        val (srcRow, srcCol, dstRow, dstCol) = moveRequest

        if (srcRow == dstRow && srcCol == dstCol) throw ChessRuleException("Kif, you fool! You're moving a piece right back where it was!")
        if (!Utils.tileOnBoard(srcRow, srcCol)) throw ChessRuleException("Kif, what have I told you about reaching for pieces off the board?")
        if (!Utils.tileOnBoard(dstRow, dstCol)) throw ChessRuleException("Kif, if you'd like to move a piece off the board, you should just give up.")

        val movingPiece = board[srcRow][srcCol] ?: throw ChessRuleException("Kif, what have I told you about moving a piece from an empty tile?")
        val movingColor = movingPiece.color
        val opposingColor = Utils.getOpposingColor(movingColor)

        var move = tryEnPassant(board, movingPiece, moveRequest)

        move = move ?: tryStandardMove(board, movingPiece, moveRequest)

        // if the mover is in check, the move is invalid
        if (inCheckAfterMove(game, board, movingColor, move)) throw ChessRuleException("Kif, you can't do that! You're in cheque!")

        // if the opponent is in check after the move, update the game state
        val newStatus = if (inCheckAfterMove(game, board, opposingColor, move)) {
            if (opposingColor == PieceColor.BLACK) GameStatus.BLACK_CHECK else GameStatus.WHITE_CHECK
        } else {
            if (opposingColor == PieceColor.BLACK) GameStatus.WHITE_TURN else GameStatus.BLACK_CHECK
        }
        updateGameStatus(game, newStatus)

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

        val moveError = if (targetPiece === null) canMove(board, movingPiece, dst) else canCapture(board, movingPiece, dst)
        moveError?.let { throw ChessRuleException(it) }

        if (targetPiece?.color == movingPiece.color) throw ChessRuleException("Kif, if I can't kill my own men anymore, neither can you.")

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

    private fun applyMoveToBoard(board: Array<Array<Piece?>>, move: Move): Array<Array<Piece?>> {
        val copiedBoard = Utils.copyBoard(board)

        copiedBoard[move.srcRow][move.srcCol] = null
        copiedBoard[move.dstRow][move.dstCol] = move.movingPiece

        val takenLocation = move.takenPiece?.position()
        takenLocation?.let { copiedBoard[it.row][it.col] == null }

        if (move.moveType == MoveType.EN_PASSANT) copiedBoard[move.srcRow][move.srcCol] = null
        if (move.moveType == MoveType.KING_SIDE_CASTLE) {
            // TODO write test for this case after implementing
            copiedBoard[move.srcRow][move.dstCol - 1] = copiedBoard[move.srcRow][move.dstCol + 1]
            copiedBoard[move.srcRow][move.dstCol + 1] = null
        }
        if (move.moveType == MoveType.QUEEN_SIDE_CASTLE) {
            // TODO write test for this case after implementing
            copiedBoard[move.srcRow][move.dstCol + 1] = copiedBoard[move.srcRow][move.dstCol - 2]
            copiedBoard[move.srcRow][move.dstCol - 2] = null
        }

        return copiedBoard
    }

    private fun inCheckAfterMove(game: Game, board: Array<Array<Piece?>>, color: PieceColor, move: Move): Boolean {
        val king = pieceService.findAllBy(game.id, color, type = PieceType.KING).first()
        var opposingPieces = pieceService.findAllBy(game.id, Utils.getOpposingColor(color), taken = false)
        opposingPieces = opposingPieces.filter { it.id != move.takenPiece?.id }
        opposingPieces.forEach {
            if (it.id == move.movingPiece.id) {
                it.positionCol = move.dstCol
                it.positionRow = move.dstRow
            }
        }
        var kingPosition = king.position() ?: return false // should never happen
        if (move.movingPiece.id == king.id) {
            kingPosition = Position(move.dstRow, move.dstCol)
        }

        val afterMoveBoard = applyMoveToBoard(board, move)

        return opposingPieces.any { canCapture(afterMoveBoard, it, kingPosition) == null }
    }

    private fun canCapture(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position): String? = canAct(board, movingPiece, dst, movingPiece::canCapture)

    private fun canMove(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position): String? = canAct(board, movingPiece, dst, movingPiece::canMove)

    private fun canAct(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position, canActFn: (dst: Position) -> Boolean): String? {
        val plausibleMove = canActFn(dst)
        if (!plausibleMove) return "Kif, I don't think that piece moves like that."

        val requiredEmpty = movingPiece.requiresEmpty(dst)
        if(requiredEmpty.any { board[it.row][it.col] != null }) return "Kif, that piece is being blocked by another."

        return null
    }

    private fun updateGameStatus(game: Game, status: GameStatus): Game {
        game.status = status
        return gameRepository.save(game)
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
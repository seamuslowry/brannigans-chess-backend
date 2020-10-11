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

    fun updateGameStatus(gameId: Long, movingColor: PieceColor): Game {
        val game = gameRepository.getOne(gameId)
        val newStatus = getGameStatusAfterMove(game, Utils.getOpposingColor(movingColor))
        return updateGameStatus(game, newStatus)
    }

    private fun move(game: Game, moveRequest: MoveRequest): Move {
        val board = pieceService.getPiecesAsBoard(game.id)
        val (srcRow, srcCol, dstRow, dstCol) = moveRequest

        if (srcRow == dstRow && srcCol == dstCol) throw ChessRuleException("Kif, you fool! You're moving a piece right back where it was!")
        if (!Utils.tileOnBoard(srcRow, srcCol)) throw ChessRuleException("Kif, what have I told you about reaching for pieces off the board?")
        if (!Utils.tileOnBoard(dstRow, dstCol)) throw ChessRuleException("Kif, if you'd like to move a piece off the board, you should just give up.")

        val movingPiece = board[srcRow][srcCol] ?: throw ChessRuleException("Kif, what have I told you about moving a piece from an empty tile?")

        val gameStatus = game.status
        if (
                (movingPiece.color == PieceColor.BLACK && !(gameStatus == GameStatus.BLACK_TURN || gameStatus == GameStatus.BLACK_CHECK))
                || (movingPiece.color == PieceColor.WHITE && !(gameStatus == GameStatus.WHITE_TURN || gameStatus == GameStatus.WHITE_CHECK))
        ) throw ChessRuleException("Slow down, Kif it's my turn. Or at least not yours.")



        val opposingColor = Utils.getOpposingColor(movingPiece.color)

        val move = tryMove(game, board, movingPiece, moveRequest)

        val appliedMove = applyMove(game, move)

        // update the game state to reflect which player's turn and check status
        val newStatus = getGameStatusAfterMove(game, opposingColor)
        updateGameStatus(game, newStatus)

        return appliedMove
    }

    private fun tryMove(game: Game, board: Array<Array<Piece?>>, movingPiece: Piece, moveRequest: MoveRequest): Move {
        var move = tryEnPassant(board, movingPiece, moveRequest)

        move = move ?: tryCastle(game, board, movingPiece, moveRequest)

        move = move ?: tryStandardMove(board, movingPiece, moveRequest)

        // if the mover is in check, the move is invalid
        if (inCheckAfterMove(game, board, movingPiece.color, move)) throw ChessRuleException("Kif, you can't do that! You're in cheque!")

        return move
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

    private fun tryCastle(game: Game, board: Array<Array<Piece?>>, movingPiece: Piece, moveRequest: MoveRequest): Move? {
        if (movingPiece is King) {
            val (srcRow, srcCol, dstRow, dstCol) = moveRequest
            val (moveType, rookPosition) = when (Position(dstRow, dstCol)) {
                movingPiece.kingSideCastleRequest() -> Pair(MoveType.KING_SIDE_CASTLE, movingPiece.kingSideCastleRook())
                movingPiece.queenSideCastleRequest() -> Pair(MoveType.QUEEN_SIDE_CASTLE, movingPiece.queenSideCastleRook())
                else -> return null
            }

            // cannot castle if in check
            if (game.status == GameStatus.BLACK_CHECK || game.status == GameStatus.WHITE_CHECK) return null

            // cannot castle if king has already moved
            if (moveService.hasMoved(movingPiece)) return null

            // cannot castle if rook has already moved
            val rook = board[rookPosition.row][rookPosition.col]
            if (rook == null || moveService.hasMoved(rook)) return null

            // cannot castle if tiles between the king and rook are occupied
            val requiresEmpty = movingPiece.requiresEmpty(rookPosition)
            if (requiresEmpty.any { board[it.row][it.col] != null }) return null

            // cannot castle if any of the intervening tiles would place the king in check
            if (requiresEmpty.any {
                        val spoofedMove = Move(movingPiece, srcRow, srcCol, it.row, it.col)
                        inCheckAfterMove(game, board, movingPiece.color, spoofedMove)
                    }) return null

            return Move(
                    movingPiece,
                    srcRow,
                    srcCol,
                    dstRow,
                    dstCol,
                    null,
                    moveType
            )
        }

        return null
    }

    private fun tryStandardMove(board: Array<Array<Piece?>>, movingPiece: Piece, moveRequest: MoveRequest): Move {
        val (srcRow, srcCol, dstRow, dstCol) = moveRequest
        val targetPiece = board[dstRow][dstCol]
        val dst = Position(dstRow, dstCol)

        val actError = if (targetPiece === null) moveError(board, movingPiece, dst) else captureError(board, movingPiece, dst)
        actError?.let { throw ChessRuleException(it) }

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

    private fun applyMove(game: Game, move: Move): Move {
        val takenPiece = move.takenPiece?.let {
            pieceService.takePiece(it)
        }
        val savedMovingPiece = pieceService.movePiece(move.movingPiece, move.dstRow, move.dstCol)
        if (move.moveType == MoveType.KING_SIDE_CASTLE) {
            val castle = pieceService.getPieceAt(game.id, move.srcRow, move.dstCol + 1)
            castle?.let { pieceService.movePiece(castle, move.srcRow, move.dstCol - 1) }
        }
        if (move.moveType == MoveType.QUEEN_SIDE_CASTLE) {
            val castle = pieceService.getPieceAt(game.id, move.srcRow, move.dstCol - 2)
            castle?.let { pieceService.movePiece(castle, move.srcRow, move.dstCol + 1) }
        }

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

        if (move.moveType == MoveType.EN_PASSANT) copiedBoard[move.srcRow][move.dstCol] = null
        if (move.moveType == MoveType.KING_SIDE_CASTLE) {
            copiedBoard[move.srcRow][move.dstCol - 1] = copiedBoard[move.srcRow][move.dstCol + 1]
            copiedBoard[move.srcRow][move.dstCol + 1] = null
        }
        if (move.moveType == MoveType.QUEEN_SIDE_CASTLE) {
            copiedBoard[move.srcRow][move.dstCol + 1] = copiedBoard[move.srcRow][move.dstCol - 2]
            copiedBoard[move.srcRow][move.dstCol - 2] = null
        }

        return copiedBoard
    }

    private fun applyMoveToPieces(pieces: Iterable<Piece>, move: Move): Iterable<Piece> {
        var newPieces = pieces.map { it.copy() }
        newPieces = newPieces.filter { it.id != move.takenPiece?.id }

        newPieces.forEach {
            if (it.id == move.movingPiece.id) {
                it.positionCol = move.dstCol
                it.positionRow = move.dstRow
            }
        }

        if (move.moveType == MoveType.EN_PASSANT) {
            newPieces = newPieces.filter { !(it.positionCol == move.dstCol && it.positionRow == move.srcRow) }
        }
        // castle checks are NOT necessary here; method used only to calculate check

        return newPieces
    }

    private fun applyMoveToPiece(piece: Piece, move: Move) = applyMoveToPieces(listOf(piece), move).first()

    private fun haveAnyValidMoves(game: Game, board: Array<Array<Piece?>>, pieces: Iterable<Piece>): Boolean = pieces.any {piece ->
        piece.plausibleMoves().any { pos ->
            try {
                val piecePos = piece.position()
                piecePos?.let { tryMove(game, board, piece, MoveRequest(it.row, it.col, pos.row, pos.col)) }
                true
            } catch (ex: ChessRuleException) {
                false
            }
        }
    }


    private fun inCheckAfterMove(game: Game, board: Array<Array<Piece?>>, color: PieceColor, move: Move): Boolean {
        // find the king after the move
        var king = pieceService.findAllBy(game.id, color, type = PieceType.KING).first()
        // find all the active opposing pieces
        var opposingPieces = pieceService.findAllBy(game.id, Utils.getOpposingColor(color), status = PieceStatus.ACTIVE)

        // get the opposing pieces as they would look after the move
        opposingPieces = applyMoveToPieces(opposingPieces, move)
        // get the king as it would look after the move
        king = applyMoveToPiece(king, move)
        // get the 2D board array after the move is applied
        val afterMoveBoard = applyMoveToBoard(board, move)

        return canBeCaptured(afterMoveBoard, king, opposingPieces)
    }

    private fun canBeCaptured(board: Array<Array<Piece?>>, king: Piece, pieces: Iterable<Piece>): Boolean {
        // get the after-move king's position
        val kingPosition = king.position() ?: return false // should never happen

        // can any opposing piece capture the king if the move were applied
        return pieces.any { canCapture(board, it, kingPosition) }
    }

    private fun canCapture(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position): Boolean = captureError(board, movingPiece, dst) == null

    private fun moveError(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position): String? = actError(board, movingPiece, dst, movingPiece::canMove)

    private fun captureError(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position): String? = actError(board, movingPiece, dst, movingPiece::canCapture)

    private fun actError(board: Array<Array<Piece?>>, movingPiece: Piece, dst: Position, canActFn: (dst: Position) -> Boolean): String? {
        val plausibleMove = canActFn(dst)
        if (!plausibleMove) return "Kif, I don't think that piece moves like that."

        val requiredEmpty = movingPiece.requiresEmpty(dst)
        if(requiredEmpty.any { board[it.row][it.col] != null }) return "Kif, that piece is being blocked by another."

        return null
    }

    fun getGameStatusAfterMove(game: Game, opposingColor: PieceColor): GameStatus {
        val board = pieceService.getPiecesAsBoard(game.id)
        val friendlyPieces = pieceService.findAllBy(game.id, opposingColor, PieceStatus.ACTIVE)
        val opposingPieces = pieceService.findAllBy(game.id, Utils.getOpposingColor(opposingColor), PieceStatus.ACTIVE)
        val king = pieceService.findAllBy(game.id, opposingColor, type = PieceType.KING).first()

        val inCheck = canBeCaptured(board, king, opposingPieces)
        val hasValidMove = haveAnyValidMoves(game, board, friendlyPieces)

        val promotable = opposingPieces.any { it is Pawn && it.promotable() }

        return when {
            inCheck && hasValidMove -> if (opposingColor == PieceColor.BLACK) GameStatus.BLACK_CHECK else GameStatus.WHITE_CHECK
            inCheck && !hasValidMove -> GameStatus.CHECKMATE
            !inCheck && !hasValidMove -> GameStatus.STALEMATE
            promotable -> if (opposingColor == PieceColor.BLACK) GameStatus.WHITE_PROMOTION else GameStatus.BLACK_PROMOTION
            else -> if (opposingColor == PieceColor.BLACK) GameStatus.BLACK_TURN else GameStatus.WHITE_TURN
        }
    }

    fun updateGameStatus(game: Game, newStatus: GameStatus): Game {
        if (newStatus == GameStatus.CHECKMATE) {
            game.winner = if (game.status === GameStatus.WHITE_TURN) game.whitePlayer else game.blackPlayer
        }
        game.status = newStatus
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
package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.utils.Utils
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@WebMvcTest(GameService::class)
@AutoConfigureMockMvc(addFilters = false)
class GameServiceTest {
    @MockkBean
    lateinit var gameRepository: GameRepository
    @MockkBean
    lateinit var pieceService: PieceService
    @MockkBean
    lateinit var moveService: MoveService

    @Autowired
    private lateinit var service: GameService

    @BeforeEach
    fun init () {
        every { moveService.createMove(any()) } answers { firstArg() }
        every { pieceService.getPiecesAsBoard(any()) } returns Utils.getEmptyBoard()
        every { gameRepository.getOne(any()) } answers { Game("Generic Game", id=firstArg()) }
        every { gameRepository.save(any<Game>()) } answers { firstArg() }
        every { pieceService.findAllBy(any(), any(), any(), any()) } answers { listOf(King(secondArg(), null, id=100)) }
        every { moveService.hasMoved(any()) } returns false
    }

    @Test
    fun `creates a game with all the pieces`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game, 0, 0)
        every { gameRepository.save(any<Game>()) } returns game
        every { pieceService.createPiece(any()) } returns piece

        val newGame = service.createGame()

        verify(exactly = 1) { gameRepository.save(any<Game>()) }
        verify(exactly = 32) { pieceService.createPiece(any()) }
        assertEquals(game.uuid , newGame.uuid)
    }

    @Test
    fun `searches for games`() {
        val game = Game("Search Game")
        every { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) } returns PageImpl(listOf(game))

        val foundPieces = service.findAllBy(true, Pageable.unpaged())

        verify(exactly = 1) { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `throws an exception on a move to the same location`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,0,0))
        }
    }

    @Test
    fun `throws an exception on a move from a row below the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(-1,0,0,0))
        }
    }

    @Test
    fun `throws an exception on a move from a row above the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(8,0,0,0))
        }
    }

    @Test
    fun `throws an exception on a move from a col before the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,-1,0,0))
        }
    }

    @Test
    fun `throws an exception on a move from a col after the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,8,0,0))
        }
    }

    @Test
    fun `throws an exception on a move to a row below the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,-1,0))
        }
    }

    @Test
    fun `throws an exception on a move to a row above the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,8,0))
        }
    }

    @Test
    fun `throws an exception on a move to a col before the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,0,-1))
        }
    }

    @Test
    fun `throws an exception on a move to a col after the board`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,0,8))
        }
    }

    @Test
    fun `throws an exception on a move from an empty tile`() {
        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,1,0))
        }
    }

    @Test
    fun `throws an exception on a move that a piece cannot plausibly perform`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val pawn = Pawn(PieceColor.BLACK, game, 1, 0)
        gameBoard[1][0] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(0,0,5,0))
        }
    }

    @Test
    fun `throws an exception when attempting to move over a tile that must be empty`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val pawn = Pawn(PieceColor.BLACK, game, 1, 0)
        val blockingPawn = Pawn(PieceColor.WHITE, game, 2, 0)
        gameBoard[1][0] = pawn
        gameBoard[2][0] = blockingPawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,3,0))
        }
    }

    @Test
    fun `throws an exception when trying to take a piece of the same color`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val pawn = Pawn(PieceColor.BLACK, game, 1, 0)
        val targetPawn = Pawn(PieceColor.BLACK, game, 2, 1)
        gameBoard[1][0] = pawn
        gameBoard[2][1] = targetPawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,2,1))
        }
    }

    @Test
    fun `throws an exception when the king moves into check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val king = King(PieceColor.WHITE, game, 7, 4, id=89)
        val pawn = Pawn(PieceColor.BLACK, game, 5, 5, id=12)
        gameBoard[7][4] = king
        gameBoard[5][5] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, false, any()) } returns listOf(pawn)

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(7,4,6,4))
        }
    }

    @Test
    fun `throws an exception when the moving unrelated piece while in check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val king = King(PieceColor.WHITE, game, 6, 4)
        val pawn = Pawn(PieceColor.BLACK, game, 5, 5, id=12)
        gameBoard[7][4] = king
        gameBoard[5][5] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(pawn)

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(6,1,6,3))
        }
    }

    @Test
    fun `moves a piece`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val pawn = Pawn(PieceColor.BLACK, game, 1, 0, id=13)
        gameBoard[1][0] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        val move = service.move(1, MoveRequest(1,0,3,0))

        assertEquals(move.movingPiece, pawn)
    }

    @Test
    fun `takes a piece on a move`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        gameBoard[1][0] = Pawn(PieceColor.BLACK, game, 1, 0)
        gameBoard[1][1] = Rook(PieceColor.WHITE, game, 1, 1)

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}

        val move = service.move(1, MoveRequest(1,1,1,0))

        assertEquals(move.takenPiece, gameBoard[1][0])
        assertEquals(move.movingPiece, gameBoard[1][1])
    }

    @Test
    fun `will en passant`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("En Passant Board")
        val passantTarget = Pawn(PieceColor.BLACK, game, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game, 3, 3)
        gameBoard[3][2] = passantTarget
        gameBoard[3][3] = passantAttacker

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { moveService.findLastMove(any()) } returns Move(passantTarget, 1,2,3,2)

        val move = service.move(1, MoveRequest(3,3,2,2))

        assertEquals(MoveType.EN_PASSANT, move.moveType)
        assertEquals(move.takenPiece, passantTarget)
        assertEquals(move.movingPiece, passantAttacker)
    }

    @Test
    fun `will not en passant a non-pawn`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("En Passant Board")
        val passantTarget = Rook(PieceColor.BLACK, game, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game, 3, 3)
        gameBoard[3][2] = passantTarget
        gameBoard[3][3] = passantAttacker

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { moveService.findLastMove(any()) } returns Move(passantTarget, 1,2,3,2)

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(3,3,2,2))
        }
    }

    @Test
    fun `will not en passant a pawn that only moved one`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("En Passant Board")
        val passantTarget = Rook(PieceColor.BLACK, game, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game, 3, 3)
        gameBoard[3][2] = passantTarget
        gameBoard[3][3] = passantAttacker

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { moveService.findLastMove(any()) } returns Move(passantTarget, 2,2,3,2)

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(3,3,2,2))
        }
    }

    @Test
    fun `will not en passant a pawn that did not move recently`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("En Passant Board")
        val passantTarget = Rook(PieceColor.BLACK, game, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game, 3, 3)
        val unrelatedPawn = Pawn(PieceColor.BLACK, game, 3, 7)
        gameBoard[3][2] = passantTarget
        gameBoard[3][3] = passantAttacker
        gameBoard[3][7] = unrelatedPawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { moveService.findLastMove(any()) } returns Move(unrelatedPawn, 1,7,3,7)

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(3,3,2,2))
        }
    }

    @Test
    fun `allows the king to kill the piece keeping it in check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board")
        val king = King(PieceColor.WHITE, game, 7, 4, id=95)
        val pawn = Pawn(PieceColor.BLACK, game, 6, 5, id=12)
        gameBoard[7][4] = king
        gameBoard[6][5] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, false, any()) } returns listOf(pawn)

        val move = service.move(1, MoveRequest(7,4,6,5))

        assertEquals(pawn, move.takenPiece)
    }

    @Test
    fun `allows a piece to move to protect the king`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board")
        val king = King(PieceColor.WHITE, game, 7, 4, id=99)
        val queen = Queen(PieceColor.WHITE, game, 7, 3, id=90)
        val rook = Rook(PieceColor.BLACK, game, 5, 4, id=12)
        gameBoard[7][4] = king
        gameBoard[7][3] = queen
        gameBoard[5][4] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, false, any()) } returns listOf(rook)

        val move = service.move(1, MoveRequest(7,3,6,4))

        assertNull(move.takenPiece)
        assertEquals(move.movingPiece.type, PieceType.QUEEN)
    }

    @Test
    fun `can en passant to check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board")
        val king = King(PieceColor.WHITE, game, 6, 4, id=98)
        val passantPawn = Pawn(PieceColor.WHITE, game, 4, 5)
        val pawn = Pawn(PieceColor.BLACK, game, 4, 6, id=12)
        gameBoard[6][4] = king
        gameBoard[4][5] = passantPawn
        gameBoard[4][6] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { moveService.findLastMove(any()) } returns Move(passantPawn, 2,5,4,5)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(pawn)

        val move = service.move(1, MoveRequest(4,6,5,5))

        verify(exactly = 1) { gameRepository.save(any<Game>()) }
        assertEquals(passantPawn.id, move.takenPiece?.id)
    }

    @Test
    fun `will not castle while in check - WHITE`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board", status = GameStatus.WHITE_CHECK)
        val king = King(PieceColor.WHITE, game, 7, 4, id=98)
        gameBoard[7][4] = king

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns emptyList()
        every { gameRepository.getOne(any()) } returns game

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(7,4,7,6)) }
    }

    @Test
    fun `will not castle while in check - BLACK`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board", status = GameStatus.BLACK_CHECK)
        val king = King(PieceColor.BLACK, game, 0, 4, id=102)
        gameBoard[0][4] = king

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns emptyList()
        every { gameRepository.getOne(any()) } returns game

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(0,4,0,6)) }
    }

    @Test
    fun `will not castle if king has already moved`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=105)
        gameBoard[0][4] = king

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns emptyList()
        every { moveService.hasMoved(king) } returns true

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(0,4,0,6)) }
    }

    @Test
    fun `will not castle if rook has already moved`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=106)
        val rook = Rook(PieceColor.BLACK, game, 0, 7, id=107)
        gameBoard[0][4] = king
        gameBoard[0][7] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns emptyList()
        every { moveService.hasMoved(king) } returns false
        every { moveService.hasMoved(rook) } returns true

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(0,4,0,6)) }
    }

    @Test
    fun `will not castle if rook cannot be found`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=108)
        val rook = Rook(PieceColor.BLACK, game, 1, 7, id=109)
        gameBoard[0][4] = king
        gameBoard[1][7] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns emptyList()
        every { moveService.hasMoved(king) } returns false

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(0,4,0,6)) }
    }

    @Test
    fun `will not castle if intervening tile is occupied`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game, 0, 7, id=111)
        val bishop = Bishop(PieceColor.BLACK, game, 0, 5, id=112)
        gameBoard[0][4] = king
        gameBoard[0][5] = bishop
        gameBoard[0][7] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns emptyList()
        every { moveService.hasMoved(any()) } returns false

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(0,4,0,6)) }
    }

    @Test
    fun `will not castle if intervening tile would be in check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game, 0, 7, id=111)
        val whiteRook = Rook(PieceColor.WHITE, game, 3, 5, id=112)
        gameBoard[0][4] = king
        gameBoard[3][5] = whiteRook
        gameBoard[0][7] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns listOf(whiteRook)
        every { moveService.hasMoved(any()) } returns false

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(0,4,0,6)) }
    }

    @Test
    fun `king side castles`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game, 0, 7, id=111)
        gameBoard[0][4] = king
        gameBoard[0][7] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king, rook)

        val move = service.move(1, MoveRequest(0,4,0,6))

        assertEquals(king, move.movingPiece)
        assertEquals(MoveType.KING_SIDE_CASTLE, move.moveType)
    }

    @Test
    fun `queen side castles`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board")
        val king = King(PieceColor.BLACK, game, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game, 0, 0, id=111)
        gameBoard[0][4] = king
        gameBoard[0][0] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king, rook)

        val move = service.move(1, MoveRequest(0,4,0,2))

        assertEquals(king, move.movingPiece)
        assertEquals(MoveType.QUEEN_SIDE_CASTLE, move.moveType)
    }
}
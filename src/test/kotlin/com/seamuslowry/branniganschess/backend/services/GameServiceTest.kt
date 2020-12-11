package com.seamuslowry.branniganschess.backend.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.GameStateException
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.models.pieces.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.utils.Utils
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
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
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

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
        every { gameRepository.getOne(any()) } answers { Game("Generic Game Id", id=firstArg(), status = GameStatus.WHITE_TURN) }
        every { gameRepository.findOne(any()) } answers { Optional.of(Game("Generic Game Uuid", status = GameStatus.WHITE_TURN)) }
        every { gameRepository.save(any<Game>()) } answers { firstArg() }
        every { pieceService.findAllBy(any(), any<PieceColor>(), any(), any()) } answers { listOf(King(secondArg(), 0L, positionCol = 4, positionRow = if (secondArg<PieceColor>() === PieceColor.BLACK) 0 else 7, id=100)) }
        every { moveService.hasMoved(any()) } returns false
    }

    @Test
    fun `creates a game with all the pieces`() {
        val game = Game("New Game")
        val piece = Pawn(PieceColor.BLACK, game.id, 0, 0)
        every { gameRepository.save(any<Game>()) } returns game
        every { pieceService.createPiece(any()) } returns piece

        val newGame = service.createGame()

        verify(exactly = 1) { gameRepository.save(any<Game>()) }
        verify(exactly = 32) { pieceService.createPiece(any()) }
        assertEquals(game.uuid , newGame.uuid)
    }

    @Test
    fun `gets a single game by id`() {
        val game = Game("New Game")

        every { gameRepository.getOne(any()) } returns game
        val newGame = service.getById(game.id)

        assertEquals(game, newGame)
    }

    @Test
    fun `gets a single game by uuid`() {
        val game = Game("New Game")

        every { gameRepository.findOne(any()) } returns Optional.of(game)
        val newGame = service.getByUuid(game.uuid)

        assertEquals(game, newGame)
    }

    @Test
    @WithMockUser(username = "def-not-in-game")
    fun `gets all game data`() {
        val game = Game("New Game")
        val piece = Rook(PieceColor.WHITE, game.id)
        val move = Move(piece, 0, 0, 0, 1)

        every { gameRepository.findOne(any()) } returns Optional.of(game)
        every { pieceService.findAllBy(any<Game>()) } returns listOf(piece)
        every { moveService.findAllBy(any<Game>()) } returns listOf(move)
        val gameData = service.getAllGameData(game.uuid)

        assertEquals(game, gameData.game)
        assertTrue(gameData.pieces.contains(piece))
        assertTrue(gameData.moves.contains(move))
    }

    @Test
    fun `searches for games`() {
        val game = Game("Search Game")
        every { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) } returns PageImpl(listOf(game))

        val foundPieces = service.findAllBy(pageable =  Pageable.unpaged())

        verify(exactly = 1) { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `searches for black color games by a player`() {
        val game = Game("Search Game")
        val player = Player(System.nanoTime().toString())
        every { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) } returns PageImpl(listOf(game))

        val foundPieces = service.findPlayerGames(player, PieceColor.BLACK, emptyList(), Pageable.unpaged())

        verify(exactly = 1) { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `searches for white color games by a player`() {
        val game = Game("Search Game")
        val player = Player(System.nanoTime().toString())
        every { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) } returns PageImpl(listOf(game))

        val foundPieces = service.findPlayerGames(player, PieceColor.WHITE, emptyList(), Pageable.unpaged())

        verify(exactly = 1) { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `searches for games by a player`() {
        val game = Game("Search Game")
        val player = Player(System.nanoTime().toString())
        every { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) } returns PageImpl(listOf(game))

        val foundPieces = service.findPlayerGames(player, pageable = Pageable.unpaged())

        verify(exactly = 1) { gameRepository.findAll(any<Specification<Game>>(), any<Pageable>()) }
        assertEquals(1 , foundPieces.count())
    }

    @Test
    fun `counts games for a player`() {
        val player = Player(System.nanoTime().toString())
        val count: Long = 101
        every { gameRepository.count(any<Specification<Game>>()) } returns count

        val gamesCount = service.countPlayerGames(player, PieceColor.WHITE, listOf(GameStatus.STALEMATE))

        verify(exactly = 1) { gameRepository.count(any<Specification<Game>>()) }
        assertEquals(count, gamesCount)
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
    fun `throws an exception when black moves off its turn`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.WHITE_TURN)
        val pawn = Pawn(PieceColor.BLACK, game.id, 1, 0)
        gameBoard[1][0] = pawn

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,2,0))
        }
    }

    @Test
    fun `throws an exception when white moves off its turn`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.BLACK_TURN)
        val pawn = Pawn(PieceColor.WHITE, game.id, 1, 0)
        gameBoard[1][0] = pawn

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,2,0))
        }
    }

    @Test
    fun `throws an exception when black moves off its turn - white check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.WHITE_CHECK)
        val pawn = Pawn(PieceColor.BLACK, game.id, 1, 0)
        gameBoard[1][0] = pawn

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,2,0))
        }
    }

    @Test
    fun `throws an exception when black moves off its turn - black check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.BLACK_CHECK)
        val pawn = Pawn(PieceColor.WHITE, game.id, 1, 0)
        gameBoard[1][0] = pawn

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,2,0))
        }
    }

    @Test
    fun `throws an exception on a move that a piece cannot plausibly perform`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.BLACK_TURN)
        val pawn = Pawn(PieceColor.BLACK, game.id, 1, 0)
        gameBoard[1][0] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(1,0,5,0))
        }
    }

    @Test
    fun `throws an exception when attempting to move over a tile that must be empty`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.BLACK_TURN)
        val pawn = Pawn(PieceColor.BLACK, game.id, 1, 0)
        val blockingPawn = Pawn(PieceColor.WHITE, game.id, 2, 0)
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
        val game = Game("Moving Board", status = GameStatus.BLACK_TURN)
        val pawn = Pawn(PieceColor.BLACK, game.id, 1, 0)
        val targetPawn = Pawn(PieceColor.BLACK, game.id, 2, 1)
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
        val game = Game("Moving Board", status = GameStatus.WHITE_TURN)
        val king = King(PieceColor.WHITE, game.id, 7, 4, id=89)
        val pawn = Pawn(PieceColor.BLACK, game.id, 5, 5, id=12)
        gameBoard[7][4] = king
        gameBoard[5][5] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, PieceStatus.ACTIVE, any()) } returns listOf(pawn)

        assertThrows<ChessRuleException> {
            service.move(1, MoveRequest(7,4,6,4))
        }
    }

    @Test
    fun `throws an exception when the moving unrelated piece while in check`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.WHITE_TURN)
        val king = King(PieceColor.WHITE, game.id, 6, 4)
        val pawn = Pawn(PieceColor.BLACK, game.id, 5, 5, id=12)
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
        val game = Game("Moving Board", status = GameStatus.BLACK_TURN)
        val pawn = Pawn(PieceColor.BLACK, game.id, 1, 0, id=13)
        gameBoard[1][0] = pawn

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}

        val move = service.move(1, MoveRequest(1,0,3,0))

        assertEquals(move.movingPiece, pawn)
    }

    @Test
    fun `takes a piece on a move`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board", status = GameStatus.WHITE_TURN)
        gameBoard[1][0] = Pawn(PieceColor.BLACK, game.id, 1, 0)
        gameBoard[1][1] = Rook(PieceColor.WHITE, game.id, 1, 1)

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
        val game = Game("En Passant Board", status = GameStatus.WHITE_TURN)
        val passantTarget = Pawn(PieceColor.BLACK, game.id, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game.id, 3, 3)
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
        val game = Game("En Passant Board", status = GameStatus.WHITE_TURN)
        val passantTarget = Rook(PieceColor.BLACK, game.id, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game.id, 3, 3)
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
        val game = Game("En Passant Board", status = GameStatus.WHITE_TURN)
        val passantTarget = Rook(PieceColor.BLACK, game.id, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game.id, 3, 3)
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
        val game = Game("En Passant Board", status = GameStatus.WHITE_TURN)
        val passantTarget = Rook(PieceColor.BLACK, game.id, 3, 2)
        val passantAttacker = Pawn(PieceColor.WHITE, game.id, 3, 3)
        val unrelatedPawn = Pawn(PieceColor.BLACK, game.id, 3, 7)
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
        val game = Game("Check Board", status = GameStatus.WHITE_TURN)
        val king = King(PieceColor.WHITE, game.id, 7, 4, id=95)
        val pawn = Pawn(PieceColor.BLACK, game.id, 6, 5, id=12)
        gameBoard[7][4] = king
        gameBoard[6][5] = pawn

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, PieceStatus.TAKEN, any()) } returns listOf(pawn)

        val move = service.move(1, MoveRequest(7,4,6,5))

        assertEquals(pawn, move.takenPiece)
    }

    @Test
    fun `allows a piece to move to protect the king`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board", status = GameStatus.WHITE_TURN)
        val king = King(PieceColor.WHITE, game.id, 7, 4, id=99)
        val queen = Queen(PieceColor.WHITE, game.id, 7, 3, id=90)
        val rook = Rook(PieceColor.BLACK, game.id, 5, 4, id=12)
        gameBoard[7][4] = king
        gameBoard[7][3] = queen
        gameBoard[5][4] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, PieceStatus.ACTIVE, any()) } returns listOf(rook)

        val move = service.move(1, MoveRequest(7,3,6,4))

        assertNull(move.takenPiece)
        assertEquals(move.movingPiece.type, PieceType.QUEEN)
    }

    @Test
    fun `will not castle while in check - WHITE`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Check Board", status = GameStatus.WHITE_CHECK)
        val king = King(PieceColor.WHITE, game.id, 7, 4, id=98)
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
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=102)
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
        val game = Game("Castle Board", status = GameStatus.BLACK_TURN)
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=105)
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
        val game = Game("Castle Board", status = GameStatus.BLACK_TURN)
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=106)
        val rook = Rook(PieceColor.BLACK, game.id, 0, 7, id=107)
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
        val game = Game("Castle Board", status = GameStatus.BLACK_TURN)
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=108)
        val rook = Rook(PieceColor.BLACK, game.id, 1, 7, id=109)
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
        val game = Game("Castle Board", status = GameStatus.BLACK_TURN)
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game.id, 0, 7, id=111)
        val bishop = Bishop(PieceColor.BLACK, game.id, 0, 5, id=112)
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
        val game = Game("Castle Board", status = GameStatus.WHITE_TURN)
        val king = King(PieceColor.WHITE, game.id, 7, 4, id=110)
        val rook = Rook(PieceColor.WHITE, game.id, 7, 7, id=111)
        val whiteRook = Rook(PieceColor.BLACK, game.id, 4, 5, id=112)
        gameBoard[7][4] = king
        gameBoard[4][5] = whiteRook
        gameBoard[7][7] = rook

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(whiteRook)
        every { moveService.hasMoved(any()) } returns false

        assertThrows<ChessRuleException> { service.move(1, MoveRequest(7,4,7,6)) }
    }

    @Test
    fun `king side castles`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board", status = GameStatus.BLACK_TURN)
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game.id, 0, 7, id=111)
        gameBoard[0][4] = king
        gameBoard[0][7] = rook

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king, rook)
        every { pieceService.getPieceAt(any(), 0,7) } returns rook

        val move = service.move(1, MoveRequest(0,4,0,6))

        assertEquals(king, move.movingPiece)
        assertEquals(MoveType.KING_SIDE_CASTLE, move.moveType)
    }

    @Test
    fun `queen side castles`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Castle Board", status = GameStatus.BLACK_TURN)
        val king = King(PieceColor.BLACK, game.id, 0, 4, id=110)
        val rook = Rook(PieceColor.BLACK, game.id, 0, 0, id=111)
        gameBoard[0][4] = king
        gameBoard[0][0] = rook

        every { gameRepository.getOne(any()) } returns game
        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king, rook)
        every { pieceService.getPieceAt(any(), 0,0) } returns rook

        val move = service.move(1, MoveRequest(0,4,0,2))

        assertEquals(king, move.movingPiece)
        assertEquals(MoveType.QUEEN_SIDE_CASTLE, move.moveType)
    }

    @Test
    fun `finds a white checkmate`() {
        val board = Utils.getEmptyBoard()
        val game = Game("White Checkmate Board")
        val king = King(PieceColor.BLACK, game.id, 0, 0, id=1)
        val rookOne = Rook(PieceColor.WHITE, game.id, 1, 6, id=2)
        val rookTwo = Rook(PieceColor.WHITE, game.id, 0, 7, id=3)

        board[0][0] = king
        board[1][6] = rookOne
        board[0][7] = rookTwo

        every { pieceService.getPiecesAsBoard(any()) } returns board
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns listOf(rookOne, rookTwo)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, PieceStatus.ACTIVE, any()) } returns listOf(king)

        val newStatus = service.getGameStatusForNextPlayer(game, PieceColor.BLACK)

        assertEquals(GameStatus.WHITE_CHECKMATE, newStatus)
    }

    @Test
    fun `finds a black checkmate`() {
        val board = Utils.getEmptyBoard()
        val game = Game("Black Checkmate Board")
        val king = King(PieceColor.WHITE, game.id, 0, 0, id=1)
        val rookOne = Rook(PieceColor.BLACK, game.id, 1, 6, id=2)
        val rookTwo = Rook(PieceColor.BLACK, game.id, 0, 7, id=3)

        board[0][0] = king
        board[1][6] = rookOne
        board[0][7] = rookTwo

        every { pieceService.getPiecesAsBoard(any()) } returns board
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), PieceType.KING) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(rookOne, rookTwo)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, PieceStatus.ACTIVE, any()) } returns listOf(king)

        val newStatus = service.getGameStatusForNextPlayer(game, PieceColor.WHITE)

        assertEquals(GameStatus.BLACK_CHECKMATE, newStatus)
    }

    @Test
    fun `finds a stalemate`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Stalemate Board")
        val king = King(PieceColor.BLACK, game.id, 0, 0, id=1)
        val rookOne = Rook(PieceColor.WHITE, game.id, 1, 6, id=2)
        val rookTwo = Rook(PieceColor.WHITE, game.id, 2, 1, id=3)
        gameBoard[0][0] = king
        gameBoard[1][6] = rookOne
        gameBoard[2][1] = rookTwo

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns listOf(rookOne, rookTwo)

        val newStatus = service.getGameStatusForNextPlayer(game, PieceColor.BLACK)

        assertEquals(GameStatus.STALEMATE, newStatus)
    }

    @Test
    fun `finds a promotion state - WHITE`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Promotion Board")
        val pawn = Pawn(PieceColor.WHITE, game.id, 0, 1, id=2)
        val king = King(PieceColor.BLACK, game.id, 7, 0, id=1)
        gameBoard[0][1] = pawn
        gameBoard[7][0] = king

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns listOf(pawn)

        val newStatus = service.getGameStatusForNextPlayer(game, PieceColor.BLACK)

        assertEquals(GameStatus.WHITE_PROMOTION, newStatus)
    }

    @Test
    fun `finds a promotion state - BLACK`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Promotion Board")
        val pawn = Pawn(PieceColor.BLACK, game.id, 7, 1, id=2)
        val king = King(PieceColor.WHITE, game.id, 0, 0, id=1)
        gameBoard[7][1] = pawn
        gameBoard[0][0] = king

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(pawn)

        val newStatus = service.getGameStatusForNextPlayer(game, PieceColor.WHITE)

        assertEquals(GameStatus.BLACK_PROMOTION, newStatus)
    }

    @Test
    fun `updates to a stalemate`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Stalemate Board")
        val king = King(PieceColor.BLACK, game.id, 0, 0, id=1)
        val rookOne = Rook(PieceColor.WHITE, game.id, 1, 6, id=2)
        val rookTwo = Rook(PieceColor.WHITE, game.id, 2, 1, id=3)
        gameBoard[0][0] = king
        gameBoard[1][6] = rookOne
        gameBoard[2][1] = rookTwo

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.findAllBy(any(), PieceColor.BLACK, any(), any()) } returns listOf(king)
        every { pieceService.findAllBy(any(), PieceColor.WHITE, any(), any()) } returns listOf(rookOne, rookTwo)

        val savedGame = service.updateGameStatusForNextPlayer(game.id, PieceColor.BLACK)

        assertEquals(GameStatus.STALEMATE, savedGame.status)
    }

    @Test
    fun `adds a white player as requested`() {
        val player = Player("success-add-white")
        val savedGame = service.addPlayer("1", player, PieceColor.WHITE)

        assertEquals(GameStatus.WAITING_FOR_BLACK, savedGame.status)
        assertEquals(player, savedGame.whitePlayer)
    }

    @Test
    fun `adds a black player as requested`() {
        val player = Player("success-add-black")
        val savedGame = service.addPlayer("1", player, PieceColor.BLACK)

        assertEquals(GameStatus.WAITING_FOR_WHITE, savedGame.status)
        assertEquals(player, savedGame.blackPlayer)
    }

    @Test
    fun `adds a white player by default`() {
        val player = Player("success-default-white")

        val savedGame = service.addPlayer("1", player)

        assertEquals(GameStatus.WAITING_FOR_BLACK, savedGame.status)
        assertEquals(player, savedGame.whitePlayer)
    }

    @Test
    fun `adds a black player by default`() {
        val player = Player("success-default-black")
        val game = Game("white-filled-game", whitePlayer = Player("existing-white"))

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        val savedGame = service.addPlayer("1", player)

        assertEquals(GameStatus.WHITE_TURN, savedGame.status)
        assertEquals(player, savedGame.blackPlayer)
    }

    @Test
    fun `will not add to a full game`() {
        val game = Game("full-game", blackPlayer = Player("full-black"), whitePlayer = Player("full-white"))

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        assertThrows<GameStateException> { service.addPlayer("1", Player("disallow-full")) }
    }

    @Test
    fun `will not add a white player when white is assigned`() {
        val game = Game("white-filled-game", whitePlayer = Player("no-overwrite-white"))

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        assertThrows<GameStateException> { service.addPlayer("1", Player("try-overwrite-white"), PieceColor.WHITE) }
    }

    @Test
    fun `will not add a black player when black is assigned`() {
        val game = Game("black-filled-game", blackPlayer = Player("no-overwrite-black"))

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        assertThrows<GameStateException> { service.addPlayer("1", Player("try-overwrite-black"), PieceColor.BLACK) }
    }

    @Test
    fun `swaps a player from white to black`() {
        val player = Player("try-swap-black")
        val game = Game("black-swap-game", whitePlayer = player)

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        val savedGame = service.addPlayer("1", player, PieceColor.BLACK)

        assertEquals(GameStatus.WAITING_FOR_WHITE, savedGame.status)
        assertEquals(player, savedGame.blackPlayer)
    }

    @Test
    fun `swaps a player from black to white`() {
        val player = Player("try-swap-white")
        val game = Game("white-swap-game", blackPlayer = player)

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        val savedGame = service.addPlayer("1", player, PieceColor.WHITE)

        assertEquals(GameStatus.WAITING_FOR_BLACK, savedGame.status)
        assertEquals(player, savedGame.whitePlayer)
    }

    @Test
    fun `does not try to re-add a player`() {
        val player = Player("try-readd")
        val game = Game("readd-game", blackPlayer = player)

        every { gameRepository.findOne(any()) } returns Optional.of(game)

        val savedGame = service.addPlayer("1", player)

        assertEquals(GameStatus.WAITING_FOR_WHITE, savedGame.status)
        assertEquals(player, savedGame.blackPlayer)
    }

    @Test
    fun `removes a black player`() {
        val player = Player("remove-black")
        val game = Game("remove-black-game", blackPlayer = player)

        every { gameRepository.getOne(any()) } returns game

        val savedGame = service.removePlayer(1, player)

        assertEquals(GameStatus.WAITING_FOR_PLAYERS, savedGame.status)
        assertNull(savedGame.blackPlayer)
    }

    @Test
    fun `removes a white player`() {
        val player = Player("remove-white")
        val game = Game("remove-white-game", whitePlayer = player)

        every { gameRepository.getOne(any()) } returns game

        val savedGame = service.removePlayer(1, player)

        assertEquals(GameStatus.WAITING_FOR_PLAYERS, savedGame.status)
        assertNull(savedGame.whitePlayer)
    }

    @Test
    fun `will not remove from a full game`() {
        val player = Player("no-remove")
        val game = Game("no-remove-game", whitePlayer = player, blackPlayer = Player("no-remove-black"))

        every { gameRepository.getOne(any()) } returns game

        assertThrows<GameStateException> { service.removePlayer(1, player) }
    }
}
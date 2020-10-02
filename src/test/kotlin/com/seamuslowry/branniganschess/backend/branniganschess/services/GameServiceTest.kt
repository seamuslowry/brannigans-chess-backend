package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.branniganschess.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.branniganschess.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.branniganschess.models.pieces.Rook
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.utils.Utils
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
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
        every { moveService.createMove(any()) } answers {firstArg()}
        every { pieceService.getPiecesAsBoard(any()) } returns Utils.getEmptyBoard()
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
    fun `moves a piece`() {
        val gameBoard = Utils.getEmptyBoard()
        val game = Game("Moving Board")
        val pawn = Pawn(PieceColor.BLACK, game, 1, 0)
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
        gameBoard[0][0] = Rook(PieceColor.BLACK, game, 0, 0)
        gameBoard[1][1] = Pawn(PieceColor.WHITE, game, 1, 1)

        every { pieceService.getPiecesAsBoard(any()) } returns gameBoard
        every { pieceService.movePiece(any(), any(), any()) } answers {firstArg()}
        every { pieceService.takePiece(any()) } answers {firstArg()}

        val move = service.move(1, MoveRequest(1,1,0,0))

        assertEquals(move.takenPiece, gameBoard[0][0])
        assertEquals(move.movingPiece, gameBoard[1][1])
    }
}
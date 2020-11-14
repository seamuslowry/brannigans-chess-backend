package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.PieceStatus
import com.seamuslowry.branniganschess.backend.models.PieceType
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PawnPromotionIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `will promote a pawn into check - WHITE`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][3]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen
        pieceService.takePiece(board[0][3]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 3)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("type") { value(PieceType.QUEEN.toString()) }
        }

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(pawn.id, savedPawn.id)
        assertEquals(GameStatus.BLACK_CHECK, savedGame.status)
    }

    @Test
    fun `will promote a pawn into check - BLACK`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[1][3]!!

        // set up a valid test by performing invalid moves through the service
        // take the white queen
        pieceService.takePiece(board[7][3]!!)
        // move black pawn into a position where it could promote
        pieceService.movePiece(pawn, 7, 3)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt().jwt { it.claim("sub", game.blackPlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("type") { value(PieceType.QUEEN.toString()) }
        }

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(pawn.id, savedPawn.id)
        assertEquals(GameStatus.WHITE_CHECK, savedGame.status)
    }

    @Test
    fun `will promote a pawn - WHITE`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen side bishop
        pieceService.takePiece(board[0][2]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 2)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("type") { value(PieceType.QUEEN.toString()) }
        }

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(pawn.id, savedPawn.id)
        assertEquals(GameStatus.BLACK_TURN, savedGame.status)
    }

    @Test
    fun `will promote a pawn - BLACK`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[1][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the white queen side bishop
        pieceService.takePiece(board[7][2]!!)
        // move black pawn into a position where it could promote
        pieceService.movePiece(pawn, 7, 2)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt().jwt { it.claim("sub", game.blackPlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("type") { value(PieceType.QUEEN.toString()) }
        }

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(pawn.id, savedPawn.id)
        assertEquals(GameStatus.WHITE_TURN, savedGame.status)
    }

    @Test
    fun `will not promote a white pawn - BLACK`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen side bishop
        pieceService.takePiece(board[0][2]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 2)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt().jwt { it.claim("sub", game.blackPlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `will promote a white pawn - BLACK`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[1][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the white queen side bishop
        pieceService.takePiece(board[7][2]!!)
        // move black pawn into a position where it could promote
        pieceService.movePiece(pawn, 7, 2)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `will not promote a white pawn - SPECTATOR`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen side bishop
        pieceService.takePiece(board[0][2]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 2)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt())
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `will promote a white pawn - SPECTATOR`() {
        val game = testUtils.createFullGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[1][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the white queen side bishop
        pieceService.takePiece(board[7][2]!!)
        // move black pawn into a position where it could promote
        pieceService.movePiece(pawn, 7, 2)

        // promote
        mockMvc.post("/pieces/promote/${pawn.id}/QUEEN") {
            with(jwt())
        }.andExpect {
            status { isForbidden }
        }
    }
}
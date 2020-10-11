package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.PieceIdentifierDto
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.models.Piece
import com.seamuslowry.branniganschess.backend.models.PieceStatus
import com.seamuslowry.branniganschess.backend.models.pieces.Queen
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PawnPromotionIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `will promote a pawn into check - WHITE`() {
        val game = gameService.createGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][3]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen
        pieceService.takePiece(board[0][3]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 3)

        // promote
        val response = restTemplate.postForEntity(
                "/pieces/promote/QUEEN",
                PieceIdentifierDto(game.id, 0, 3),
                Piece::class.java
        )

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pawn.id, savedPawn.id)
        assertTrue(response.body is Queen)
        assertEquals(GameStatus.BLACK_CHECK, savedGame.status)
    }

    @Test
    fun `will promote a pawn into check - BLACK`() {
        val game = gameService.createGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[1][3]!!

        // set up a valid test by performing invalid moves through the service
        // take the white queen
        pieceService.takePiece(board[7][3]!!)
        // move black pawn into a position where it could promote
        pieceService.movePiece(pawn, 7, 3)

        // promote
        val response = restTemplate.postForEntity(
                "/pieces/promote/QUEEN",
                PieceIdentifierDto(game.id, 7, 3),
                Piece::class.java
        )

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pawn.id, savedPawn.id)
        assertTrue(response.body is Queen)
        assertEquals(GameStatus.WHITE_CHECK, savedGame.status)
    }

    @Test
    fun `will promote a pawn - WHITE`() {
        val game = gameService.createGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[6][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the black queen side bishop
        pieceService.takePiece(board[0][2]!!)
        // move white pawn into a position where it could promote
        pieceService.movePiece(pawn, 0, 2)

        // promote
        val response = restTemplate.postForEntity(
                "/pieces/promote/QUEEN",
                PieceIdentifierDto(game.id, 0, 2),
                Piece::class.java
        )

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pawn.id, savedPawn.id)
        assertTrue(response.body is Queen)
        assertEquals(GameStatus.BLACK_TURN, savedGame.status)
    }

    @Test
    fun `will promote a pawn - BLACK`() {
        val game = gameService.createGame()
        val board = pieceService.getPiecesAsBoard(game.id)
        val pawn = board[1][2]!!

        // set up a valid test by performing invalid moves through the service
        // take the white queen side bishop
        pieceService.takePiece(board[7][2]!!)
        // move black pawn into a position where it could promote
        pieceService.movePiece(pawn, 7, 2)

        // promote
        val response = restTemplate.postForEntity(
                "/pieces/promote/QUEEN",
                PieceIdentifierDto(game.id, 7, 2),
                Piece::class.java
        )

        val savedPawn = pieceService.findAllBy(game.id, status = PieceStatus.REMOVED).first()
        val savedGame = gameRepository.getOne(game.id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pawn.id, savedPawn.id)
        assertTrue(response.body is Queen)
        assertEquals(GameStatus.WHITE_TURN, savedGame.status)
    }
}
package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchPiecesIntegrationTest(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameRepository: GameRepository,
        @Autowired val pieceService: PieceService
) {
    @Test
    fun `Finds pieces from a specific game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))
        val gameTwo = gameRepository.save(Game("Piece Search I-Test Game Two"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameTwo))

        val entity = restTemplate.getForEntity("/pieces/${gameOne.id}", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${searchPiece.id}"))
    }

    @Test
    fun `Finds pieces of a specific color from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne))

        val entity = restTemplate.getForEntity("/pieces/${gameOne.id}?color=BLACK", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${searchPiece.id}"))
    }

    @Test
    fun `Finds taken pieces from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne, 0, 0, true))
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))

        val entity = restTemplate.getForEntity("/pieces/${gameOne.id}?taken=true", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${searchPiece.id}"))
    }

    @Test
    fun `Finds taken pieces of a specific color from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))
        val gameTwo = gameRepository.save(Game("Piece Search I-Test Game Two"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne, 0, 0, true))
        // matches game and taken
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne, 0, 0, true))
        // matches game and color
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne, 0, 0, false))
        // matches color and taken
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameTwo, 0, 0, true))

        val entity = restTemplate.getForEntity("/pieces/${gameOne.id}?color=BLACK&taken=true", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${searchPiece.id}"))
    }
}
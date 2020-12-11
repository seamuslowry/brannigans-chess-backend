package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.pieces.King
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.models.pieces.Rook
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SystemSearchPiecesIntegrationTests(
        @Autowired val gameRepository: GameRepository,
        @Autowired val pieceService: PieceService,
        @Autowired val gameService: GameService
) {
    @Test
    fun `Finds a color king in a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne))
        val searchPiece = pieceService.createPiece(King(PieceColor.BLACK, gameOne))

        val foundPieces = pieceService.findAllBy(gameOne.id, searchPiece.color, type = searchPiece.type)

        Assertions.assertEquals(1, foundPieces.count())
        Assertions.assertEquals(searchPiece.id, foundPieces.first().id)
    }

    @Test
    fun `Finds a piece at a location`() {
        val gameOne = gameService.createGame()

        val foundPiece = pieceService.getPieceAt(gameOne.id, 0,7)

        Assertions.assertTrue(foundPiece is Rook)
        Assertions.assertEquals(0, foundPiece?.positionRow)
        Assertions.assertEquals(7, foundPiece?.positionCol)
    }
}
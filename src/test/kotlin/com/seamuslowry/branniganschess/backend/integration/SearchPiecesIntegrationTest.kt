package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.PieceStatus
import com.seamuslowry.branniganschess.backend.models.pieces.King
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.models.pieces.Rook
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SearchPiecesIntegrationTest(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameRepository: GameRepository,
        @Autowired val pieceService: PieceService,
        @Autowired val gameService: GameService
) {
    @Test
    fun `Will not finds pieces from a specific game without color specified`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))
        val gameTwo = gameRepository.save(Game("Piece Search I-Test Game Two"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameTwo))

        mockMvc.get("/pieces/${gameOne.id}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(0) }
        }
    }

    @Test
    fun `Finds pieces of neither color from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val blackPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        val whitePiece = pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne))

        mockMvc.get("/pieces/${gameOne.id}") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(0) }
            jsonPath("$[?(@.id == ${blackPiece.id})]") { isEmpty }
            jsonPath("$[?(@.id == ${whitePiece.id})]") { isEmpty }
        }
    }

    @Test
    fun `Finds pieces of a specific color from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne))

        mockMvc.get("/pieces/${gameOne.id}?color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${searchPiece.id})]") { isNotEmpty }
        }
    }

    @Test
    fun `Finds pieces of both colors from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val blackPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))
        val whitePiece = pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne))

        mockMvc.get("/pieces/${gameOne.id}?color=BLACK&color=WHITE") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[?(@.id == ${blackPiece.id})]") { isNotEmpty }
            jsonPath("$[?(@.id == ${whitePiece.id})]") { isNotEmpty }
        }
    }

    @Test
    fun `Finds on taken pieces from a game without a specified color`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne, 0, 0, PieceStatus.TAKEN))
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))

        mockMvc.get("/pieces/${gameOne.id}?status=TAKEN") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(0) }
        }
    }

    @Test
    fun `Finds taken pieces of a specific color from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))
        val gameTwo = gameRepository.save(Game("Piece Search I-Test Game Two"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne, 0, 0, PieceStatus.TAKEN))
        // matches game and taken
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne, 0, 0, PieceStatus.TAKEN))
        // matches game and color
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne, 0, 0, PieceStatus.ACTIVE))
        // matches color and taken
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameTwo, 0, 0, PieceStatus.TAKEN))

        mockMvc.get("/pieces/${gameOne.id}?color=BLACK&status=TAKEN") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${searchPiece.id})]") { isNotEmpty }
        }
    }

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
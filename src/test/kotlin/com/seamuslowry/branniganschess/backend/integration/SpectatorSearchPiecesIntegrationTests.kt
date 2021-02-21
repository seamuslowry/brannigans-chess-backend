package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.PieceStatus
import com.seamuslowry.branniganschess.backend.models.pieces.Pawn
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SpectatorSearchPiecesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameRepository: GameRepository,
        @Autowired val pieceService: PieceService
) {
    @Test
    fun `Will not find pieces from a specific game without color specified`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))
        val gameTwo = gameRepository.save(Game("Piece Search I-Test Game Two"))

        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameTwo.id))

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

        val blackPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id))
        val whitePiece = pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne.id))

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
    fun `Finds black pieces from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id))
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne.id))

        mockMvc.get("/pieces/${gameOne.id}?color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${searchPiece.id})]") { isNotEmpty }
        }
    }

    @Test
    fun `Finds white pieces from a game`() {
        val gameOne = gameRepository.save(Game("Piece Search I-Test Game One"))

        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id))
        val searchPiece = pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne.id))

        mockMvc.get("/pieces/${gameOne.id}?color=WHITE") {
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

        val blackPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id))
        val whitePiece = pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne.id))

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

        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id, 0, 0, PieceStatus.TAKEN))
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id))

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

        val searchPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id, 0, 0, PieceStatus.TAKEN))
        // matches game and taken
        pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne.id, 0, 0, PieceStatus.TAKEN))
        // matches game and color
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne.id, 0, 0, PieceStatus.ACTIVE))
        // matches color and taken
        pieceService.createPiece(Pawn(PieceColor.BLACK, gameTwo.id, 0, 0, PieceStatus.TAKEN))

        mockMvc.get("/pieces/${gameOne.id}?color=BLACK&status=TAKEN") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${searchPiece.id})]") { isNotEmpty }
        }
    }
}
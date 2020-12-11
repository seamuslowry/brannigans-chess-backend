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
class GetPiecesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameRepository: GameRepository,
        @Autowired val pieceService: PieceService
) {
    @Test
    fun `White pieces send up game id and uuid`() {
        val gameOne = gameRepository.save(Game("White Piece Game Fields", id=1))

        val whitePiece = pieceService.createPiece(Pawn(PieceColor.WHITE, gameOne))

        mockMvc.get("/pieces/${gameOne.uuid}?color=WHITE") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${whitePiece.id})]") { isNotEmpty }
            jsonPath("$[?(@.gameId == '${gameOne.id}')]") { isNotEmpty }
            jsonPath("$[?(@.gameUuid == '${gameOne.uuid}')]") { isNotEmpty }
        }
    }

    @Test
    fun `Black pieces send up game id and uuid`() {
        val gameOne = gameRepository.save(Game("Black Piece Game Fields", id=1))

        val blackPiece = pieceService.createPiece(Pawn(PieceColor.BLACK, gameOne))

        mockMvc.get("/pieces/${gameOne.uuid}?color=BLACK") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[?(@.id == ${blackPiece.id})]") { isNotEmpty }
            jsonPath("$[?(@.gameId == '${gameOne.id}')]") { isNotEmpty }
            jsonPath("$[?(@.gameUuid == '${gameOne.uuid}')]") { isNotEmpty }
        }
    }
}
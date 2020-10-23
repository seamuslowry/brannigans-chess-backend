package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.models.GameStatus
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CheckMoveIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val gameService: GameService,
        @Autowired val pieceService: PieceService,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `puts black in check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid move through the service
        // move a pawn out of the way
        pieceService.movePiece(board[1][4]!!, 3,3)
        // move a rook into ready to check
        pieceService.movePiece(board[7][0]!!, 4,3)

        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(4,3,4,4))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.BLACK_CHECK, game.status)
    }

    @Test
    fun `puts white in check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid move through the service
        // move a pawn out of the way
        pieceService.movePiece(board[6][4]!!, 3,3)
        // move a rook into ready to check
        pieceService.movePiece(board[0][0]!!, 4,3)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(4,3,4,4))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.WHITE_CHECK, game.status)
    }

    @Test
    fun `will en passant to place opponent in check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid moves through the service
        // move white pawn into a position where it could en passant
        pieceService.movePiece(board[6][3]!!, 3,3)
        // move a black pawn out of the way for the king
        pieceService.movePiece(board[1][1]!!, 4,1)
        // move the king to the pawn's former space
        pieceService.movePiece(board[0][4]!!, 1,1)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)
        // move the target pawn in prep for the take
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(1,2,3,2))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
        }

        // take with en passant
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(3,3,2,2))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isNotEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.BLACK_CHECK, game.status)
    }

    @Test
    fun `cannot move into check`() {
        val game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid moves through the service
        // move white rook into an open position
        pieceService.movePiece(board[7][0]!!, 3,3)
        // move the king near check
        pieceService.movePiece(board[0][4]!!, 2,2)

        // attempt to move the king into check
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(2,2,2,3))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isBadRequest }
        }
    }

    @Test
    fun `cannot make unrelated move while in check`() {
        val game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid moves through the service
        // move white rook into an open position
        pieceService.movePiece(board[7][0]!!, 3,3)
        // move the king to the pawn's former space
        pieceService.movePiece(board[0][4]!!, 3,2)

        // attempt to move an unrelated pawn
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(1,6,2,6))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isBadRequest }
        }
    }

    @Test
    fun `can defend king from check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid moves through the service
        // move black rook into an open position
        pieceService.movePiece(board[0][0]!!, 3,4)
        // take the pawn that would defend the king
        pieceService.takePiece(board[6][4]!!)

        // attempt to use queen to protect the king
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(7,3,6,4))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertNotEquals(GameStatus.WHITE_CHECK, game.status)
    }

    @Test
    fun `can move out of check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing an invalid moves through the service
        // move white rook into an open position
        pieceService.movePiece(board[7][0]!!, 3,3)
        // move the king into check
        pieceService.movePiece(board[0][4]!!, 3,2)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_CHECK)
        // attempt to move the king out of check
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(3,2,2,2))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertNotEquals(GameStatus.BLACK_CHECK, game.status)
    }

    @Test
    fun `king can take a piece placing it in check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // move white rook into an open position
        pieceService.movePiece(board[7][0]!!, 3,3)
        // move the black king into check
        pieceService.movePiece(board[0][4]!!, 3,2)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_CHECK)
        // attempt to take rook
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(3,2,3,3))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isNotEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertNotEquals(GameStatus.BLACK_CHECK, game.status)
    }

    @Test
    fun `non-king can take a piece placing it in check`() {
        var game = createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take pawn in front of white king
        pieceService.takePiece(board[6][4]!!)
        // move black rook into newly open position
        pieceService.movePiece(board[0][0]!!, 6,4)

        // use queen to take rook
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(7,3,6,4))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("movingPiece") { isNotEmpty }
            jsonPath("takenPiece") { isNotEmpty }
        }

        game = gameRepository.getOne(game.id)

        assertNotEquals(GameStatus.WHITE_CHECK, game.status)
    }

    @Test
    fun `can castle to place opponent in check`() {
        var game = gameService.createGame()
        val board = pieceService.getPiecesAsBoard(game.id)

        // set up a valid test by performing invalid moves through the service
        // take black king-side bishop
        pieceService.takePiece(board[0][5]!!)
        // take black king-side knight
        pieceService.takePiece(board[0][6]!!)
        // take black pawn in front of where black rook will end
        pieceService.takePiece(board[1][5]!!)
        // take white pawn in front of where black rook will end
        pieceService.takePiece(board[6][5]!!)
        // take white king-side bishop
        pieceService.takePiece(board[7][5]!!)
        // move white king in front of where rook will end up
        pieceService.movePiece(board[7][4]!!, 7, 5)

        // set it to be black's turn
        gameService.updateGameStatusForNextPlayer(game, GameStatus.BLACK_TURN)
        // castle
        mockMvc.post("/moves/${game.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(MoveRequest(0,4,0,6))
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
        }

        game = gameRepository.getOne(game.id)

        assertEquals(GameStatus.WHITE_CHECK, game.status)
    }

    private fun createGame(): Game = gameService.createGame()
}
package com.seamuslowry.branniganschess.backend.branniganschess.services

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Piece
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceType
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@WebMvcTest(GameService::class)
@AutoConfigureMockMvc(addFilters = false)
class GameServiceTest {
    @MockkBean
    lateinit var gameRepository: GameRepository
    @MockkBean
    lateinit var pieceService: PieceService

    @Autowired
    private lateinit var service: GameService

    @Test
    fun `creates a game with all the pieces`() {
        val game = Game("New Game")
        val piece = Piece(PieceType.PAWN, PieceColor.BLACK, game, 0, 0)
        every { gameRepository.save(any<Game>()) } returns game
        every { pieceService.createPiece(any<Piece>()) } returns piece

        val newGame = service.createGame()

        verify(exactly = 1) { gameRepository.save(any<Game>()) }
        verify(exactly = 32) { pieceService.createPiece(any<Piece>()) }
        assertEquals(game.uuid , newGame.uuid)
    }
}
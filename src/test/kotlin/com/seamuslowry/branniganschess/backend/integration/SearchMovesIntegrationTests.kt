package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.services.GameService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchMovesIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val gameService: GameService
) {
    @Test
    fun `Finds all moves from a specific game`() {
        val game = gameService.createGame()
        // move white pawn up one
        val whiteMove = gameService.move(game.id, MoveRequest(6,0, 5, 0))
        // move black pawn one
        val blackMove = gameService.move(game.id, MoveRequest(1,0, 2, 0))

        val noMatchGame = gameService.createGame()
        // move white pawn up one
        gameService.move(noMatchGame.id, MoveRequest(6,0, 5, 0))

        val entity = restTemplate.getForEntity("/moves/${game.id}", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(2, entity.body?.count())
        Assertions.assertTrue(entity.body?.toString().orEmpty().contains("id=${whiteMove.id}"))
        Assertions.assertTrue(entity.body?.toString().orEmpty().contains("id=${blackMove.id}"))
    }

    @Test
    fun `Finds moves of a specific color from a game`() {
        val game = gameService.createGame()
        // move white pawn one
        gameService.move(game.id, MoveRequest(6,0, 5, 0))
        // move black pawn one
        val blackMove = gameService.move(game.id, MoveRequest(1,0, 2, 0))

        val entity = restTemplate.getForEntity("/moves/${game.id}?color=BLACK", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${blackMove.id}"))
    }
}
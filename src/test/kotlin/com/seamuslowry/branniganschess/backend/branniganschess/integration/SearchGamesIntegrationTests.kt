package com.seamuslowry.branniganschess.backend.branniganschess.integration

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import com.seamuslowry.branniganschess.backend.branniganschess.services.PieceService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchGamesIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val playerRepository: PlayerRepository,
        @Autowired val gameRepository: GameRepository,
        @Autowired val gameService: GameService
) {
    @Test
    fun `Finds active games`() {
        gameRepository.deleteAll()
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        val wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player())
        wonGame.winner = winner
        gameRepository.save(wonGame)

        val entity = restTemplate.getForEntity("/games?active=true", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${activeGame.id}"))
    }

    @Test
    fun `Finds won games`() {
        gameRepository.deleteAll()
        gameRepository.save(Game("Piece Search I-Test Active Game One"))

        var wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player())
        wonGame.winner = winner
        wonGame = gameRepository.save(wonGame)

        val entity = restTemplate.getForEntity("/games?active=false", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(1, entity.body?.count())
        Assertions.assertTrue(entity.body?.first().toString().contains("id=${wonGame.id}"))
    }

    @Test
    fun `Finds all games`() {
        gameRepository.deleteAll()
        gameRepository.save(Game("Piece Search I-Test Active Game One"))

        val wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player())
        wonGame.winner = winner
        gameRepository.save(wonGame)

        val entity = restTemplate.getForEntity("/games", Iterable::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertEquals(2, entity.body?.count())
    }
}
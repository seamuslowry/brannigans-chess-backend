package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchGamesIntegrationTests(
        @Autowired val restTemplate: TestRestTemplate,
        @Autowired val playerRepository: PlayerRepository,
        @Autowired val gameRepository: GameRepository
) {
    @Test
    fun `Finds active games`() {
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        val wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player())
        wonGame.winner = winner
        gameRepository.save(wonGame)

        val entity = restTemplate.getForEntity("/games?active=true&size=${gameRepository.count()}", TestGamePageImpl::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertNotNull(entity.body?.content?.find { it.id == activeGame.id })
        Assertions.assertNull(entity.body?.content?.find { it.id == wonGame.id })
    }

    @Test
    fun `Finds won games`() {
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        var wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player())
        wonGame.winner = winner
        wonGame = gameRepository.save(wonGame)

        val entity = restTemplate.getForEntity("/games?active=false&size=${gameRepository.count()}", TestGamePageImpl::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertNull(entity.body?.content?.find { it.id == activeGame.id })
        Assertions.assertNotNull(entity.body?.content?.find { it.id == wonGame.id })
    }

    @Test
    fun `Finds all games`() {
        val activeGame = gameRepository.save(Game("Piece Search I-Test Active Game One"))

        val wonGame = gameRepository.save(Game("Piece Search I-Test Game Two"))
        val winner = playerRepository.save(Player())
        wonGame.winner = winner
        gameRepository.save(wonGame)

        val entity = restTemplate.getForEntity("/games?size=${gameRepository.count()}", TestGamePageImpl::class.java)

        Assertions.assertEquals(HttpStatus.OK, entity.statusCode)
        Assertions.assertNotNull(entity.body?.content?.find { it.id == activeGame.id })
        Assertions.assertNotNull(entity.body?.content?.find { it.id == wonGame.id })
    }
}
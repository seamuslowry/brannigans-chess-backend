package com.seamuslowry.branniganschess.backend.branniganschess.integration

import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateGameIntegrationTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun `Create a new game`() {
        val entity = restTemplate.exchange("/games/create", HttpMethod.POST, null, Game::class.java)

        assertEquals(HttpStatus.OK, entity.statusCode)
        assertNotNull(entity.body?.id)
    }
}
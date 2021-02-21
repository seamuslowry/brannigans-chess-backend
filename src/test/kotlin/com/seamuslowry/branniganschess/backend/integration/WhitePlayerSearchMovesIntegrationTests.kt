package com.seamuslowry.branniganschess.backend.integration

import com.seamuslowry.branniganschess.backend.integration.utils.IntegrationTestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WhitePlayerSearchMovesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `Cannot see black moves from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/moves/${game.id}?color=BLACK") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `Can see white moves from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/moves/${game.id}?color=WHITE") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `Cannot find all moves from a specific game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/moves/${game.id}?color=WHITE&color=BLACK") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }
}
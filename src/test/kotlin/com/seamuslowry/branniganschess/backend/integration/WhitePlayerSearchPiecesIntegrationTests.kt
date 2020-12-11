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
class WhitePlayerSearchPiecesIntegrationTests(
        @Autowired val mockMvc: MockMvc,
        @Autowired val testUtils: IntegrationTestUtils
) {
    @Test
    fun `Cannot see black pieces from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/pieces/${game.uuid}?color=BLACK") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `Can see white pieces from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/pieces/${game.uuid}?color=WHITE") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
            jsonPath("$.length()") { value(16) }
        }
    }

    @Test
    fun `Cannot search pieces of both colors from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/pieces/${game.uuid}?color=BLACK&color=WHITE") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isForbidden }
        }
    }

    @Test
    fun `Can see black taken pieces from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/pieces/${game.uuid}?color=BLACK&status=TAKEN") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `Can see white taken pieces from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/pieces/${game.uuid}?color=WHITE&status=TAKEN") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `Can see both color taken pieces from a game`() {
        val game = testUtils.createFullGame()

        mockMvc.get("/pieces/${game.uuid}?color=WHITE&color=BLACK&status=TAKEN") {
            with(jwt().jwt { it.claim("sub", game.whitePlayer?.authId) })
        }.andExpect {
            status { isOk }
        }
    }
}
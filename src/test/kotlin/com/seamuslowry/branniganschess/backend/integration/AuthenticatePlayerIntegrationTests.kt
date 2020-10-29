package com.seamuslowry.branniganschess.backend.integration

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthenticatePlayerIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `will return a new player`() {
        val playerAuthId = "noMatchAuthId"

        mockMvc.get("/players/auth") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `returns an existing player`() {
        val playerAuthId = "matchingAuthId"

        mockMvc.get("/players/auth") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.get("/players/auth") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }
    }
}
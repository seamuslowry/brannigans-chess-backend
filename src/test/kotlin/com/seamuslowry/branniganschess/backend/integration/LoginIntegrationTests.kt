package com.seamuslowry.branniganschess.backend.integration

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LoginIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `with not return a new player`() {
        val playerAuthId = "noMatchAuthId"

        mockMvc.get("/players/login/google") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isBadRequest }
        }
    }

    @Test
    fun `returns a player after signup`() {
        val playerAuthId = "matchingAuthId"

        mockMvc.put("/players/signup/google") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.get("/players/login/google") {
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }
    }
}
package com.seamuslowry.branniganschess.backend.integration

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.put

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SignupIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `only allows a single google signup`() {
        val playerGoogleId = "singleGoogleSignInId"

        mockMvc.put("/players/signup/google") {
            with(jwt().jwt { it.claim("sub", playerGoogleId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.put("/players/signup/google") {
            with(jwt().jwt { it.claim("sub", playerGoogleId) })
        }.andExpect {
            status { isBadRequest }
        }
    }

    @Test
    fun `allows google signup`() {
        mockMvc.put("/players/signup/google") {
            with(jwt())
        }.andExpect {
            status { isOk }
        }
    }
}
package com.seamuslowry.branniganschess.backend.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CreateGameIntegrationTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `Create a new game`() {
        mockMvc.post("/games/create") {
            with(jwt())
        }.andExpect {
            status { isOk }
            jsonPath("id") { isNotEmpty }
        }
    }
}
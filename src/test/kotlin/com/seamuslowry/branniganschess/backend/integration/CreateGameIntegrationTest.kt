package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Game
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CreateGameIntegrationTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `Create a new game`() {
        mockMvc.post("/games/create") {
            with(SecurityMockMvcRequestPostProcessors.jwt())
        }.andExpect {
            status { isOk }
            jsonPath("id") { isNotEmpty }
        }
    }
}
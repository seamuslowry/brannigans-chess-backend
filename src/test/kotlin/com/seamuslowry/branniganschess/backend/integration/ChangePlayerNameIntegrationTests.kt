package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.AdditionalPlayerInfo
import com.seamuslowry.branniganschess.backend.dtos.ChangeNameDto
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ChangePlayerNameIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `will change a player's name`() {
        val playerAuthId = "noMatchAuthId"

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo("", ""))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.post("/players/name") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(ChangeNameDto("newName"))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("name") { value("newName") }
        }
    }
}
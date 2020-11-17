package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.seamuslowry.branniganschess.backend.dtos.AdditionalPlayerInfo
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
class AuthenticatePlayerIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `will return a new player`() {
        val playerAuthId = "noMatchAuthId"

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo("name", "image"))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `returns an existing player`() {
        val playerAuthId = "matchingAuthId"

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo("name", "image"))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo("name", "image"))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
        }
    }

    @Test
    fun `authenticates a player with a name and image`() {
        val playerAuthId = "newWithNameAndImage"
        val name = "New Test Person Name"
        val url = "www.new-test-image.com/image.jpg"

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo(name, url))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("name") { value(name) }
            jsonPath("imageUrl") { value(url) }
        }
    }

    @Test
    fun `does not update when authenticating a player with a new name and image`() {
        val playerAuthId = "updateWithNameAndImage"

        val originalName = "Old Test Person Name"
        val originalUrl = "www.old-test-image.com/image.jpg"

        val newName = "Updated Test Person Name"
        val newUrl = "www.updated-test-image.com/image.jpg"

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo(originalName, originalUrl))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("authId") { value(playerAuthId) }
            jsonPath("name") { value(originalName) }
            jsonPath("imageUrl") { value(originalUrl) }
        }

        mockMvc.post("/players/auth") {
            contentType = MediaType.APPLICATION_JSON
            content = ObjectMapper().writeValueAsString(AdditionalPlayerInfo(newName, newUrl))
            accept = MediaType.APPLICATION_JSON
            with(jwt().jwt { it.claim("sub", playerAuthId) })
        }.andExpect {
            status { isOk }
            jsonPath("authId") { value(playerAuthId) }
            jsonPath("name") { value(originalName) }
            jsonPath("imageUrl") { value(originalUrl) }
        }
    }
}
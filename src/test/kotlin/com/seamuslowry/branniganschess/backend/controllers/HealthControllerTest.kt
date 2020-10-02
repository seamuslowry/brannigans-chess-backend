package com.seamuslowry.branniganschess.backend.controllers

import com.ninjasquad.springmockk.MockkBean
import com.seamuslowry.branniganschess.backend.models.Game
import com.seamuslowry.branniganschess.backend.services.GameService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(HealthController::class)
@AutoConfigureMockMvc(addFilters = false)
class HealthControllerTest(@Autowired val mockMvc: MockMvc) {
    @Test
    fun `Returns a 200`() {
        mockMvc.perform(get("/health/"))
                .andExpect(status().isOk)
    }
}
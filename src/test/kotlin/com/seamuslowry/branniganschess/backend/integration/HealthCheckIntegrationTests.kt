package com.seamuslowry.branniganschess.backend.integration

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HealthCheckIntegrationTests(
    @Autowired val mockMvc: MockMvc
) {
    @Test
    fun `health check is always accessible without trailing slash`() {
        mockMvc.get("/health").andExpect {
            status { isOk }
        }
    }

    @Test
    fun `health check is always accessible with trailing slash`() {
        mockMvc.get("/health/").andExpect {
            status { isOk }
        }
    }
}
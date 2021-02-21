package com.seamuslowry.branniganschess.backend.security

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant

@ExtendWith(SpringExtension::class)
class AudienceValidatorTest {
    @Test
    fun `validates the audience - success`() {
        val jwt = createJwt()
        val validator = AudienceValidator(jwt.audience.first())
        val result = validator.validate(jwt)

        assertFalse(result.hasErrors())
    }

    @Test
    fun `validates the audience - failure`() {
        val jwt = createJwt()
        val validator = AudienceValidator("invalid-${jwt.audience.first()}")
        val result = validator.validate(jwt)

        assertTrue(result.hasErrors())
    }

    private fun createJwt() = Jwt.withTokenValue("test-token")
        .audience(listOf("audience"))
        .header("header", "test")
        .build()
}
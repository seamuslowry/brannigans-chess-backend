package com.seamuslowry.branniganschess.backend.advice

import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.SignupException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(ExceptionHandler::class)
class ExceptionHandlerTest {
    @Autowired
    private lateinit var exceptionHandler: ExceptionHandler

    @Test
    fun `Turns a ChessRuleException into a ResponseEntity`() {
        val ex = ChessRuleException("test exception")
        val entity = exceptionHandler.handleChessRuleException(ex)
        assertEquals(ex.message, entity.body)
        assertEquals(HttpStatus.BAD_REQUEST, entity.statusCode)
    }

    @Test
    fun `Turns a SignupException into a ResponseEntity`() {
        val ex = SignupException("test exception")
        val entity = exceptionHandler.handleSignupException(ex)
        assertEquals(ex.message, entity.body)
        assertEquals(HttpStatus.BAD_REQUEST, entity.statusCode)
    }
}
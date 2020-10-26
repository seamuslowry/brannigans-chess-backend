package com.seamuslowry.branniganschess.backend.advice

import com.seamuslowry.branniganschess.backend.dtos.ChessRuleException
import com.seamuslowry.branniganschess.backend.dtos.SignupException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(ChessRuleException::class)
    fun handleChessRuleException(ex: ChessRuleException): ResponseEntity<String> =
            ResponseEntity.badRequest().body(ex.message)

    @ExceptionHandler(SignupException::class)
    fun handleSignupException(ex: SignupException): ResponseEntity<String> =
        ResponseEntity.badRequest().body(ex.message)
}
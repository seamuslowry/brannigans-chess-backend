package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("health")
class HealthController {

    @GetMapping("/", "")
    @ApiOperation("Returns that the service is healthy", response = String::class)
    @ApiResponses(
            ApiResponse(code = 200, message =  "The service is up and healthy."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun checkHealth(): ResponseEntity<String> {
        return ResponseEntity.ok("Healthy - OK")
    }
}
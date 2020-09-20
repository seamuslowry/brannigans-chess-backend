package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("health")
class HealthController(
        private val gameService: GameService
) {

    @GetMapping("/")
    fun createGame(): ResponseEntity<String> {
        return ResponseEntity.ok("Healthy")
    }
}
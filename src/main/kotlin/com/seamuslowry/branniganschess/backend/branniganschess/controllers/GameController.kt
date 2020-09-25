package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("games")
class GameController(
        private val gameService: GameService
) {

    @PostMapping("/create")
    fun createGame(): ResponseEntity<Game> {
        return ResponseEntity.ok(gameService.createGame())
    }

    @GetMapping("/", "")
    fun getGames(@RequestParam(required = false) active: Boolean?)
            : ResponseEntity<Iterable<Game>> {
        return ResponseEntity.ok(gameService.findAllBy(active))
    }
}
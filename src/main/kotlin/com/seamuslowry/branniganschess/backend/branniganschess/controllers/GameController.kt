package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("games")
class GameController(
        private val gameService: GameService
) {

    @PostMapping("/create")
    fun createGame(): ResponseEntity<Game> = ResponseEntity.ok(gameService.createGame())

    @GetMapping("/", "")
    fun getGames(@RequestParam(required = false) active: Boolean?,
                 @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Iterable<Game>> = ResponseEntity.ok(gameService.findAllBy(active, pageable))
}
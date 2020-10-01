package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.data.domain.Page
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
    @ApiOperation("Creates a game", response = Game::class)
    @ApiResponses(
            ApiResponse(code = 200, message =  "The game was created."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun createGame(): ResponseEntity<Game> = ResponseEntity.ok(gameService.createGame())

    @GetMapping("/", "")
    @ApiOperation("Retrieves a page of games")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the page of games."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getGames(@RequestParam(required = false) active: Boolean?,
                 @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<Game>> = ResponseEntity.ok(gameService.findAllBy(active, pageable))
}
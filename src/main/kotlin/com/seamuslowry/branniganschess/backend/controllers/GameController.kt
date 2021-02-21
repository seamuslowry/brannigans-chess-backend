package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.dtos.AllGameData
import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.services.GameService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.annotation.SubscribeMapping
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

    @GetMapping("")
    @ApiOperation("Retrieves a page of games")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the page of games."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getGames(@RequestParam(name = "status", defaultValue = "") statuses: List<GameStatus>,
                 @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<Game>> = ResponseEntity.ok(gameService.findAllBy(statuses, pageable))

    @GetMapping("/{gameId}")
    @ApiOperation("Retrieves all data about the provided game")
    @ApiResponses(
        ApiResponse(code = 200, message =  "Successfully retrieved the game data."),
        ApiResponse(code = 401, message =  "You are not allowed to access all game data."),
        ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getAllData(@PathVariable gameId: Long): ResponseEntity<AllGameData> = ResponseEntity.ok(gameService.getAllGameData(gameId))

    /**
     * When subscribing to the game status topic, send the current game.
     *
     * @param gameId the game id
     *
     * @return the game
     */
    @SubscribeMapping("/status/{gameId}")
    fun getCurrentStatus(@DestinationVariable gameId: Long): Game {
        return gameService.getById(gameId)
    }
}
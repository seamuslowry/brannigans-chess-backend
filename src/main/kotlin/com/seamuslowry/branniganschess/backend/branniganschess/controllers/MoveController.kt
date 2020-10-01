package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.branniganschess.models.Game
import com.seamuslowry.branniganschess.backend.branniganschess.models.Move
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import com.seamuslowry.branniganschess.backend.branniganschess.services.MoveService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("moves")
class MoveController (
        private val gameService: GameService,
        private val moveService: MoveService
) {

    @PostMapping("/{gameId}")
    @ApiOperation("Tries to move a piece from one tile to another", response = Move::class)
    @ApiResponses(
            ApiResponse(code = 200, message =  "The piece was successfully moved."),
            ApiResponse(code = 400, message =  "The requested move is not a valid chess move."),
            ApiResponse(code = 404, message =  "The game does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun move(@PathVariable gameId: Long,
             @RequestBody moveRequest: MoveRequest): ResponseEntity<Move> =
            ResponseEntity.ok(gameService.move(gameId, moveRequest))

    @GetMapping("/{gameId}")
    @ApiOperation("Gets all moves for a given game", response = Move::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the list of games."),
            ApiResponse(code = 404, message =  "The game does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getMoves(@PathVariable gameId: Long,
                  @RequestParam(required = false) color: PieceColor?)
            : ResponseEntity<Iterable<Move>> {
        return ResponseEntity.ok(moveService.findAllBy(gameId, color))
    }
}
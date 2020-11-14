package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.models.Move
import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.MoveService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.simp.annotation.SubscribeMapping
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
                 @RequestParam(name = "color", defaultValue = "") colors: List<PieceColor>)
            : ResponseEntity<Iterable<Move>> {
        val game = gameService.getById(gameId)
        return ResponseEntity.ok(moveService.findAllBy(game, colors))
    }

    /**
     * When subscribing to the shared move topic, provide the last shared move.
     * This is to handle disconnects and reconnects during which a shared move
     * may have occurred.
     *
     * When returning `null`, no message is sent.
     *
     * @param gameId the id of the game to get the move of
     *
     * @return the last shared move or null if there is none
     */
    @SubscribeMapping("/moves/{gameId}")
    fun getLastSharedMove(@DestinationVariable gameId: Long): Move? {
        // does not send message with null body
        return moveService.findSharedMoves(gameId).lastOrNull()
    }
}
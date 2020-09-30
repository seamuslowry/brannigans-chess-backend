package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.dtos.MoveRequest
import com.seamuslowry.branniganschess.backend.branniganschess.models.Move
import com.seamuslowry.branniganschess.backend.branniganschess.models.PieceColor
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import com.seamuslowry.branniganschess.backend.branniganschess.services.MoveService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("moves")
class MoveController (
        private val gameService: GameService,
        private val moveService: MoveService
) {

    @PostMapping("/{gameId}")
    fun move(@PathVariable gameId: Long,
             @RequestBody moveRequest: MoveRequest): ResponseEntity<Move> =
            ResponseEntity.ok(gameService.move(gameId, moveRequest))

    @GetMapping("/{gameId}")
    fun getMoves(@PathVariable gameId: Long,
                  @RequestParam(required = false) color: PieceColor?)
            : ResponseEntity<Iterable<Move>> {
        return ResponseEntity.ok(moveService.findAllBy(gameId, color))
    }
}
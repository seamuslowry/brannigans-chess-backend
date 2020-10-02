package com.seamuslowry.branniganschess.backend.controllers

import com.seamuslowry.branniganschess.backend.models.*
import com.seamuslowry.branniganschess.backend.repos.GameRepository
import com.seamuslowry.branniganschess.backend.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.services.GameService
import com.seamuslowry.branniganschess.backend.services.PieceService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("pieces")
class PieceController(
        private val pieceService: PieceService
) {

    @GetMapping("/{gameId}")
    @ApiOperation("Gets all pieces for a given game", response = Piece::class, responseContainer = "List")
    @ApiResponses(
            ApiResponse(code = 200, message =  "Successfully retrieved the list of pieces."),
            ApiResponse(code = 404, message =  "The game does not exist."),
            ApiResponse(code = 500, message =  "There was a problem with the service.")
    )
    fun getPieces(@PathVariable gameId: Long,
                  @RequestParam(required = false) color: PieceColor?,
                  @RequestParam(required = false) taken: Boolean?)
            : ResponseEntity<Iterable<Piece>> {
        return ResponseEntity.ok(pieceService.findAllBy(gameId, color, taken))
    }
}
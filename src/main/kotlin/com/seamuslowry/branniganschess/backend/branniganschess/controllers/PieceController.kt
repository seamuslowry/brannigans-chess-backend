package com.seamuslowry.branniganschess.backend.branniganschess.controllers

import com.seamuslowry.branniganschess.backend.branniganschess.models.*
import com.seamuslowry.branniganschess.backend.branniganschess.repos.GameRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.MoveRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PieceRepository
import com.seamuslowry.branniganschess.backend.branniganschess.repos.PlayerRepository
import com.seamuslowry.branniganschess.backend.branniganschess.services.GameService
import com.seamuslowry.branniganschess.backend.branniganschess.services.PieceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("pieces")
class PieceController(
        private val pieceService: PieceService
) {

    @GetMapping("/{gameId}")
    fun getPieces(@PathVariable gameId: Long,
                  @RequestParam(required = false) color: PieceColor?,
                  @RequestParam(required = false) taken: Boolean?)
            : ResponseEntity<Iterable<Piece>> {
        return ResponseEntity.ok(pieceService.findAllBy(gameId, color, taken))
    }
}